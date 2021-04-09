// Server class.

// Package.
package placeholder;

// Imports.
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class PlaceholderServer extends DefaultSingleRecoverable {

    public PlaceholderServer(int id) {
        new ServiceReplica(id, this, this);
    }

    @Override
    public byte[] appExecuteOrdered(byte[] bytes, MessageContext context) {
        String request = new String(bytes);
        System.out.println("Requisição recebida: " + request);
        return ("Resposta - " + request).getBytes();
    }

    @Override
    public byte[] appExecuteUnordered(byte[] bytes, MessageContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] getSnapshot() {
        return "".getBytes();
    }

    @Override
    public void installSnapshot(byte[] bytes) {

    }

    public static void main(String[] args) {
        new PlaceholderServer(Integer.parseInt(args[0]));
    }

}
