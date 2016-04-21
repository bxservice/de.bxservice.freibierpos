package de.bxservice.bxpos.logic.print;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Diego Ruiz on 20/04/16.
 */
public class BluetoothPrinterService {

    private static final String TAG = "BluetoothPrinterService";

    private Activity activity;

    // android built in classes for bluetooth operations
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");


    public BluetoothPrinterService(Activity mActivity) {
        activity = mActivity;
        try {
            findBT();
            openBT();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method that finds a bluetooth printer device
    private void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                Log.i(TAG, "No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("Zebra01")) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            Log.i(TAG, "Bluetooth device found");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    private void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            Log.i(TAG, "Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * After opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
    */
    private void beginListenForData() {
        try {

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        readBufferPosition = 0;

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method that sends the text to be printed by the bluetooth printer
    public void sendData(byte[] out) {
        try {
            mmOutputStream.write(out);
            // tell the user data were sent
            Log.i(TAG, "Data sent");

        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    // Close the connection to bluetooth printer to avoid battery consumption
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.flush();
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            Log.i(TAG, "Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
