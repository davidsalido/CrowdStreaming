package com.crowdstreaming.ui.watchstreaming;

import android.os.AsyncTask;
import android.os.Looper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class StreamProxy implements Runnable {

    private static final int SERVER_PORT=8888;

    private Thread thread;
    private boolean isRunning;
    private ServerSocket socket;
    private int port;
    private static final String TAG = "tag";


    public StreamProxy() {

        try {
            socket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
            socket.setSoTimeout(5000);
            port = socket.getLocalPort();
        } catch (UnknownHostException e) {
            System.out.println("1");
        } catch (IOException e) {
            System.out.println("IOException initializing server");
        }

    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        isRunning = true;
        while (isRunning) {
            try {
                System.out.println("cliente wait");
                Socket client = socket.accept();
                if (client == null) {
                    continue;
                }
                System.out.println("client connected");

                StreamToMediaPlayerTask task = new StreamToMediaPlayerTask(client);
                if (task.processRequest()) {
                    task.execute();
                }

            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                System.out.println( "Error connecting to client");
            }
        }
        System.out.println("Proxy interrupted. Shutting down.");
    }




    private class StreamToMediaPlayerTask extends AsyncTask<String, Void, Integer> {

        String localPath;
        Socket client;
        int cbSkip = 0;

        public StreamToMediaPlayerTask(Socket client) {
            this.client = client;
        }

        public boolean processRequest() {
            // Read HTTP headers
            String headers = "";
            try {
                headers = readTextStreamAvailable(client.getInputStream());
            } catch (IOException e) {
                System.out.println( "Error reading HTTP request header from stream:");
                return false;
            }
            System.out.println(headers);
            // Get the important bits from the headers
            String[] headerLines = headers.split("\n");
            String urlLine = headerLines[0];
            if (!urlLine.startsWith("GET ")) {
                System.out.println( "Only GET is supported");
                return false;
            }
            urlLine = urlLine.substring(4);
            int charPos = urlLine.indexOf(' ');
            if (charPos != -1) {
                urlLine = urlLine.substring(1, charPos);
            }
            localPath = urlLine;
            System.out.println(localPath);


            return true;
        }

        @Override
        protected Integer doInBackground(String... params) {

            int fileSize = Integer.MAX_VALUE;

            // Create HTTP header
            String headers = "HTTP/1.0 200 OK\r\n";
            headers += "Content-Type: " + "video/mpeg2" + "\r\n";
            headers += "Content-Length: " + fileSize  + "\r\n";
            headers += "Connection: close\r\n";
            headers += "\r\n";

            // Begin with HTTP header
            int fc = 0;
            long cbToSend = fileSize;
            OutputStream output = null;
            byte[] buff = new byte[64 * 1024];
            try {
                output = new BufferedOutputStream(client.getOutputStream(), 32*1024);
                output.write(headers.getBytes());
                File file = new File(localPath);
                FileInputStream input = new FileInputStream(file);
                // Loop as long as there's stuff to send
                while (isRunning) {

                    // See if there's more to send
                    fc++;
                    if (file.exists()) {


                        int cbToSendThisBatch = input.available();

                        int cbToRead = Math.min(cbToSendThisBatch, buff.length);
                        int cbRead;
                        while ((cbRead = input.read(buff, 0, cbToRead)) > 0 && isRunning) {

                            cbToSend -= cbRead;
                            output.write(buff, 0, cbRead);
                            output.flush();

                        }

                    }

                    // If we did nothing this batch, block for a second


                }
            }
            catch (SocketException socketException) {
                socketException.printStackTrace();
            }
            catch (Exception e) {
                System.out.println( "Exception thrown from streaming task:");
                System.out.println( e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            // Cleanup
            try {
                if (output != null) {
                    output.close();
                }
                client.close();
            }
            catch (IOException  e) {
                System.out.println( "IOException while cleaning up streaming task:");
                System.out.println( e.getClass().getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            return 1;
        }

    }

    private String readTextStreamAvailable(InputStream inputStream) throws IOException
    {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);

        // Do the first byte via a blocking read
        outputStream.write(inputStream.read());

        // Slurp the rest
        int available = inputStream.available();
        while (available > 0)
        {
            int cbToRead = Math.min(buffer.length, available);
            int cbRead = inputStream.read(buffer, 0, cbToRead);
            if (cbRead <= 0)
            {
                throw new IOException("Unexpected end of stream");
            }
            outputStream.write(buffer, 0, cbRead);
            available -= cbRead;
        }
        return new String(outputStream.toByteArray());
    }
}