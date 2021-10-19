//Server (Decoder)  PacketReceiver

//Acknowledges the client that the encoded stream has been received
//Decodes teh stream and prints it on the screen

//Mirage Mohammad
//Arun Cheriakara

import java.net.*;
import java.io.*;
import java.lang.Integer;
import java.lang.String;


public class PacketReceiver{
    private DataInput input = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    //Constructor
    public PacketReceiver(int port){
        try{
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("\n -------------Server------------- \n");
            System.out.println("Connection Is Established... \n");
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String datagram = input.readUTF();
            System.out.println(datagram);
            datagram = removePadding(datagram);
            System.out.println("      ");
            System.out.println("Datagram (After Padding Removal): ");
            System.out.println(datagram);
            System.out.println("      ");
            getMessage(datagram);

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    //removePadding method
    public static String removePadding(String data){
        String [] array = data.split(" ");
        int ipLength = Integer.parseInt(array[1],16);
        data = data.substring(0,(ipLength*2)+(ipLength/2));
        System.out.println(data);
        return data;
    }


    //getMessage method
    public static void getMessage(String myStream){
        String[] message = myStream.split(" ");
        String head = message[0];
        String ipLength = message[1];
        String idField = message[2];
        String flags = message[3];
        String tcp = message[4];
        String checksum = message[5];
        String ipsrc = message[6]+" "+message[7];
        String ipdest = message[8]+" "+message[9];
        String myMessage = "";
        for(int i = 10; i<message.length;i++){
            myMessage += message[i];
        }
        //verifying if data is correct
        boolean isCorrupted = verifyChecksum(head, ipLength, idField, flags, tcp, checksum, ipsrc, ipdest);

        if(!isCorrupted){
            System.err.println("The verification of the checksum demonstrates that the packet received is corrupted. Packet discarded!");
        }
        else{
            String wordFromIPSrc = getIPAddress(ipsrc);
            String wordFromIPDest = getIPAddress(ipdest);
            int lengthOfPacket = Integer.parseInt(ipLength.substring(2,4),16); //Coverting Hexadecimal to Decimal
            int payload = Integer.parseInt(ipLength.substring(0,2),16)+20; //Coverting Hexadecimal to Decimal then adding 20
            String decodedMessage = getText(myMessage);
            //Prints
            System.out.println(" ");
            System.out.println("Receives the data stream and prints to the screen the data received with the following message: ");
            System.out.println("The data received from "+wordFromIPSrc+" is "+ decodedMessage);
            System.out.println("The data has "+(8*payload)+" bits or "+payload+" bytes. Total length of the packet is "+lengthOfPacket+" bytes.");
            System.out.println("The verification of the checksum demonstrates that the packet received is correct. ");
        }
    }

    //verifyCheckSum Method

    public static boolean verifyChecksum(String header, String lengthOfIP, String fieldID, String flags,  String tcp, String checksum, String ipsrc, String ipdest){

        //Splitting up the ips
        String ipsrc1 = ipsrc.substring(0,4);
        String ipsrc2 = ipsrc.substring(5);
        String ipdest1 = ipdest.substring(0,4);
        String ipdest2 = ipdest.substring(5);
        System.out.println(ipsrc1);
        System.out.println(ipsrc2);
        System.out.println(ipdest1);
        System.out.println(ipdest2);

        //Converting all the hexadecimals to decimals
        int headerDecimalForm = Integer.parseInt(header,16);
        System.out.println(headerDecimalForm);
        int lengthOfIPDecimalForm = Integer.parseInt(lengthOfIP,16);
        int fieldIDDecimalForm = Integer.parseInt(fieldID,16);
        int flagsDecimalForm = Integer.parseInt(flags,16);
        int tcpDecimalForm = Integer.parseInt(tcp,16);
        int checksumDecimalForm = Integer.parseInt(checksum,16);
        int sourceIP1DecimalForm = Integer.parseInt(ipsrc1,16);
        int sourceIP2DecimalForm = Integer.parseInt(ipsrc2,16);
        int destinationIP1DecimalForm = Integer.parseInt(ipdest1,16);
        int destinationIP2DecimalForm = Integer.parseInt(ipdest2,16);
        //Summation of the stream
        int summation = headerDecimalForm+lengthOfIPDecimalForm+fieldIDDecimalForm+flagsDecimalForm+tcpDecimalForm+checksumDecimalForm+sourceIP1DecimalForm+sourceIP2DecimalForm+destinationIP1DecimalForm+destinationIP2DecimalForm;
        //From java language integer.toHexString()
        //Converts the summation in decimal form to hexadecimal form
        String summationHexadecimalForm = Integer.toHexString(summation);
        System.out.println(summationHexadecimalForm);
        if(summationHexadecimalForm.length() > 4) {
            String carry = summationHexadecimalForm.substring(0, 1);//first bit
            summationHexadecimalForm = summationHexadecimalForm.substring(1); //changing summation
            int carryDecimalForm = Integer.parseInt(carry, 16); //converting hexadecimal to decimal
            int summationDecimalForm = Integer.parseInt(summationHexadecimalForm, 16);//converting hexadecimal to decimal
            //New Results
            summation = carryDecimalForm + summationDecimalForm;
            summationHexadecimalForm = Integer.toHexString(summation);
        }
        System.out.println(summationHexadecimalForm);
        if(summationHexadecimalForm.equals("ffff")){
            return true;}
        else {
            return false;
        }

        }



    //getIPAddress method
    public static String getIPAddress(String ipAddress){

        //Getting the substring and converting from hexadecimal to decimal
        int number1 = Integer.parseInt(ipAddress.substring(0,2),16);
        int number2 = Integer.parseInt(ipAddress.substring(2,4),16);
        int number3 = Integer.parseInt(ipAddress.substring(5,7),16);
        int number4 = Integer.parseInt(ipAddress.substring(7),16);

        //Returning the string representation of an ip address
        return number1+"."+number2+"."+number3+"."+number4;
    }

    //getText method
    public static String getText(String hexaMessage){
        //StringBuilder from java.lang.String
        StringBuilder stringbuilder = new StringBuilder("");
        for(int i =0; i<hexaMessage.length(); i+= 2){
            String character = hexaMessage.substring(i, i+2);
            stringbuilder.append( (char) Integer.parseInt(character,16));//char typecasting then converting hexadecimal to decimal
        }
        //Finally returns the string representation of my string builder message
        return stringbuilder.toString();
    }

    //Main Method

    public static void main(String[] args){

            //Receiver (Decoder)
            PacketReceiver server = new PacketReceiver(8888);

    }







    }


