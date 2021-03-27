// Client class.

// Package.
package placeholder;

// Imports.
import bftsmart.tom.ServiceProxy;

public class PlaceholderClient {

    public static void main(String[] args){
        ServiceProxy proxy = new ServiceProxy(1001);

        byte[] request = args[0].getBytes();
        byte[] reply = proxy.invokeOrdered(request);
        String replyString = new String(reply);

        System.out.println("Resposta recebida: " + replyString);
    }

}
