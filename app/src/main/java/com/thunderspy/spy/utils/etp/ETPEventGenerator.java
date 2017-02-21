package com.thunderspy.spy.utils.etp;

import com.thunderspy.spy.utils.Constants;
import com.thunderspy.spy.utils.Utils;
import java.util.HashMap;
import javax.net.ssl.SSLSocket;

/**
 * Created by ariyan on 2/16/17.
 */

public final class ETPEventGenerator {

    private SSLSocket sslSocket;

    private boolean nowNextEventGenerating;
    private boolean nowHeadersGenerating;
    private int howMuchDataBytesProcessed;

    private String nextHeaderLine;
    private HashMap<String, String> nextEventHeaders;

    private int nextEventDataBufferIndex;
    private byte[] nextEventDataBuffer;

    public ETPEventGenerator(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;

        this.nowNextEventGenerating = false;
        this.nowHeadersGenerating = true;
        this.howMuchDataBytesProcessed = 0;

        this.nextHeaderLine = "";
        this.nextEventHeaders = null;

        this.nextEventDataBuffer = null;
        this.nextEventDataBufferIndex = 0;
    }

    public boolean processData(byte[] data, int offset, int length) {
        boolean processed = false;
        try {

            int totalAvailableDataBytesLength = length;
            while (totalAvailableDataBytesLength > 0) {
                processed = generateNextEvent(data, offset + (length - totalAvailableDataBytesLength), totalAvailableDataBytesLength);
                if(!processed) {
                    Utils.log("Error in generating event");
                    return false;
                }
                totalAvailableDataBytesLength = totalAvailableDataBytesLength - howMuchDataBytesProcessed;
            }

            processed = true;

        } catch (Exception exp) {
            Utils.log("Error in generating event: %s", exp.getMessage());
            processed = false;
        }
        return processed;
    }

    private boolean generateNextEvent(byte[] data, int offset, int length) {
        boolean processed = false;
        try {
            if (!nowNextEventGenerating) {
                nowNextEventGenerating = true;
                nowHeadersGenerating = true;
                nextHeaderLine = "";
                nextEventHeaders = new HashMap<String, String>();
            }
            if(nowHeadersGenerating) {
                int currentOffset = offset;
                int availableDataLength = length;
                int lfFoundIndex;
                while (true) {
                    lfFoundIndex = searchLFByte(data, currentOffset, currentOffset + availableDataLength);

                    if(lfFoundIndex == -1) {
                        if(availableDataLength == 0)
                            break;
                        if((nextHeaderLine.length() + availableDataLength) > Constants.ETP_PROTOCOL_MAX_HEADER_LINE_LENGTH) {
                            Utils.log("ETP Error: header line length exceeds the maximum value");
                            return false;
                        }
                        nextHeaderLine = nextHeaderLine.concat(new String(data, currentOffset, availableDataLength, "UTF-8"));
                        currentOffset = currentOffset + availableDataLength;
                        availableDataLength = 0;
                        break;
                    } else {
                        if((nextHeaderLine.length() + (lfFoundIndex - currentOffset)) > Constants.ETP_PROTOCOL_MAX_HEADER_LINE_LENGTH) {
                            Utils.log("ETP Error: header line length exceeds the maximum value");
                            return false;
                        }
                        nextHeaderLine = nextHeaderLine.concat(new String(data, currentOffset, lfFoundIndex - currentOffset, "UTF-8"));

                        if(nextHeaderLine.length() == 0) {
                            if(!checkRequiredHeaders()) {
                                Utils.log("ETP Error: required headers are not found");
                                return false;
                            }
                            int safeEventDataLength = getSafeEventDataLength();
                            if (safeEventDataLength == -1) {
                                Utils.log("ETP Error: event data length exceeds the maximum value or invalid value");
                                return false;
                            }
                            nextEventDataBuffer = new byte[safeEventDataLength];
                            nextEventDataBufferIndex = 0;

                            availableDataLength = availableDataLength - ((lfFoundIndex - currentOffset) + 1);
                            currentOffset = lfFoundIndex + 1;
                            int requiredDataBytesLength = (nextEventDataBuffer.length - nextEventDataBufferIndex);

                            if (availableDataLength >= requiredDataBytesLength) {
                                System.arraycopy(data, currentOffset, nextEventDataBuffer, nextEventDataBufferIndex, requiredDataBytesLength);
                                availableDataLength = availableDataLength - requiredDataBytesLength;
                                currentOffset = currentOffset + requiredDataBytesLength;
                                boolean done = emitEventNow();
                                if (!done) {
                                    Utils.log("ETP Error: event could not be emitted");
                                    return false;
                                }
                            } else {
                                System.arraycopy(data, currentOffset, nextEventDataBuffer, nextEventDataBufferIndex, availableDataLength);
                                nextEventDataBufferIndex = nextEventDataBufferIndex + availableDataLength;
                                currentOffset = currentOffset + availableDataLength;
                                availableDataLength = 0;
                                nowHeadersGenerating = false;
                            }
                            break;

                        } else {
                            String[] headerParts = nextHeaderLine.split(":");
                            if(headerParts.length != 2) {
                                Utils.log("ETP Error: Invalid header line format");
                                return false;
                            }
                            String headerKey = headerParts[0].trim().toLowerCase();
                            String headerValue = headerParts[1].trim();
                            /*
                             * Ignore empty and only spaces chars key like-> " : lkk\n", ":", "    :   " nextEventHeaders.
                             * but "key1:   \n", etc applicable
                             * header value can be only space or empty
                             */
                            if(!headerKey.isEmpty()) {
                                nextEventHeaders.put(headerKey, headerValue);
                                if(nextEventHeaders.size() > Constants.ETP_PROTOCOL_MAX_HEADER_LINE_NUMBER) {
                                    Utils.log("ETP Error: header line numbers exceeds the maximum value");
                                    return false;
                                }
                            }
                            nextHeaderLine = "";
                            availableDataLength = availableDataLength - ((lfFoundIndex - currentOffset) + 1);
                            currentOffset = lfFoundIndex + 1;
                        }
                    }
                }
                howMuchDataBytesProcessed = length - availableDataLength;
            } else {
                int requiredDataBytesLength = (nextEventDataBuffer.length - nextEventDataBufferIndex);
                if (length >= requiredDataBytesLength) {
                    System.arraycopy(data, offset, nextEventDataBuffer, nextEventDataBufferIndex, requiredDataBytesLength);
                    howMuchDataBytesProcessed = requiredDataBytesLength;
                    boolean done = emitEventNow();
                    if (!done) {
                        Utils.log("ETP Error: event could not be emitted");
                        return false;
                    }
                } else {
                    System.arraycopy(data, offset, nextEventDataBuffer, nextEventDataBufferIndex, length);
                    nextEventDataBufferIndex = nextEventDataBufferIndex + length;
                    howMuchDataBytesProcessed = length;
                }
            }

            processed = true;
        } catch (Exception exp) {
            Utils.log("Error in generating next ETP event: %s", exp.getMessage());
            processed = false;
        }
        return processed;
    }

    private int searchLFByte(byte[] buffer, int fromIndex, int toIndex) {
        if(fromIndex < 0 || toIndex < 0 || fromIndex >= buffer.length || toIndex > buffer.length)
            return -1;
        int foundIndex = -1;
        for(int i = fromIndex; i<toIndex; i++) {
            if(buffer[i] == '\n') {
                foundIndex = i;
                break;
            }
        }
        return foundIndex;
    }

    private boolean checkRequiredHeaders() {
        if(nextEventHeaders == null)
            return false;
        if(!nextEventHeaders.containsKey(Constants.ETP_PROTOCOL_HEADER_NAME_EVENT_CODE.toLowerCase()))
            return false;
        if(!nextEventHeaders.containsKey(Constants.ETP_PROTOCOL_HEADER_NAME_EVENT_DATA_LENGTH.toLowerCase()))
            return false;
        return true;
    }

    /**
     *
     * @return  null if not found particular header
     */
    private String getEventCode() {
        if(nextEventHeaders == null)
            return null;
        /*
         * If exists then return value or otherwise null
         */
        return nextEventHeaders.get(Constants.ETP_PROTOCOL_HEADER_NAME_EVENT_CODE.toLowerCase());
    }

    /**
     *
     * @return  -1 if not found the particular header or any other error occurred or eventDataLength exceeds MAXIMUM VALUE.
     */
    private int getSafeEventDataLength() {
        if (nextEventHeaders == null)
            return -1;
        String eventDataLengthStr = nextEventHeaders.get(Constants.ETP_PROTOCOL_HEADER_NAME_EVENT_DATA_LENGTH.toLowerCase());
        if(eventDataLengthStr == null)
            return -1;
        try {
            int eventDataLength = Integer.parseInt(eventDataLengthStr);
            if(eventDataLength < 0)
                return -1;
            else {
                if(eventDataLength <= Constants.ETP_PROTOCOL_MAX_EVENT_DATA_LENGTH)
                    return eventDataLength;
                else
                    return -1;
            }
        } catch (Exception exp) {
            return -1;
        }
    }

    private boolean emitEventNow() {
        boolean done = false;
        try {
            String eventCode = getEventCode();
            if (eventCode == null)
                return false;
            EventCallbacks.execute(eventCode, sslSocket, nextEventHeaders, nextEventDataBuffer);

            nowNextEventGenerating = false; //Must for next event
            nextHeaderLine = ""; //Free previous event headers line [optional]
            nextEventHeaders = null; //Free previous event headers object [optional]

            this.nextEventDataBuffer = null; //Free previous event data object [optional]
            this.nextEventDataBufferIndex = 0; //[optional]

            done = true;
        } catch (Exception exp) {
            Utils.log("Error in emitting event: %s", exp.getMessage());
            done = false;
        }
        return done;
    }


}
