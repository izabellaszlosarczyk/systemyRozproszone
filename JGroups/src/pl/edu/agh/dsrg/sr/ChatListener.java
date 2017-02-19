package pl.edu.agh.dsrg.sr;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;

/**
 * Created by izabella on 14.05.16.
 */
public class ChatListener  extends ReceiverAdapter {
    private final String channelAddress;

    public ChatListener(String channelAddress) {
        this.channelAddress = channelAddress;
    }

    @Override
    public void receive(Message message) {
        byte [] buff = message.getBuffer();
        try {
            ChatOperationProtos.ChatMessage msg = ChatOperationProtos.ChatMessage.parseFrom(buff);
            System.out.println(this.channelAddress + ":" + message.getSrc() + " >> " + msg.getMessage());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
