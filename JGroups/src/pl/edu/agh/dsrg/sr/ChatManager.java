package pl.edu.agh.dsrg.sr;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.*;
import org.jgroups.stack.ProtocolStack;
import pl.edu.agh.dsrg.sr.chat.protos.ChatOperationProtos;
import pl.edu.agh.dsrg.sr.exceptions.ChannelDoNotExistsException;
import pl.edu.agh.dsrg.sr.exceptions.ChannelExistsException;
import pl.edu.agh.dsrg.sr.exceptions.ChannelNotJoinedException;

import java.io.*;
import java.util.*;

/**
 * Created by izabella on 13.05.16.
 */
public class ChatManager extends ReceiverAdapter {
    private ProtocolStack stack = new ProtocolStack();
    private final String CHAT_MANAGMENT_CHANNEL = "ChatManagement321123";
    private final String ADDRESS_PREFIX = "230.0.0.";
    private final int TIMEOUT_SEC = 10000;

    private final String login;

    private final Map<String, Set<String>> channels = new HashMap<String, Set<String>>();
    private final Map<String, JChannel> joinedChannels = new HashMap<String, JChannel>();
    private final Set<String> users = new HashSet<String>();

    private JChannel managmentChannel;

    public ChatManager(String login) {
        this.login = login;
    }

    public void start() throws Exception {
        managmentChannel = new JChannel();
        StackConfigurator.configureStack(stack);
        managmentChannel.setProtocolStack(stack);

        stack.init();

        managmentChannel.setName(this.login);
        managmentChannel.setReceiver(this);
        managmentChannel.connect(CHAT_MANAGMENT_CHANNEL);
        managmentChannel.getState(null, TIMEOUT_SEC);
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        ChatOperationProtos.ChatState.Builder builder = ChatOperationProtos.ChatState.newBuilder();
        for(Map.Entry<String, Set<String>> channelInfo : channels.entrySet()) {
            for(String nick : channelInfo.getValue()) {
                ChatOperationProtos.ChatAction action = ChatOperationProtos.ChatAction.newBuilder().setAction(ChatOperationProtos.ChatAction.ActionType.JOIN).setChannel(channelInfo.getKey()).setNickname(nick).build();
                builder.addState(action);
            }
        }
        builder.build().writeTo(output);
    }

    @Override
    public void setState(InputStream input) throws Exception {
        ChatOperationProtos.ChatState state = ChatOperationProtos.ChatState.parseFrom(input);
        for(ChatOperationProtos.ChatAction action : state.getStateList()){
            String channel = action.getChannel();
            String login = action.getNickname();
            if(!channels.containsKey(channel)) {
                channels.put(channel, new HashSet<>());
            }
            channels.get(channel).add(login);
            users.add(login);
        }
    }

    @Override
    public void viewAccepted(View newView) {
        List<String> viewUsers = new ArrayList<>();
        for(Address address : newView.getMembers()) {
            viewUsers.add(address.toString());
        }
        synchronized(users) {
            synchronized (channels) {
                for (String user : users) {
                    if (!viewUsers.contains(user)) {
                        users.remove(user);
                        for (Set<String> userList : channels.values()) {
                            userList.remove(user);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receive(Message message) {
        byte [] data = message.getBuffer();
        ChatOperationProtos.ChatAction action = null;
        try {
            action = ChatOperationProtos.ChatAction.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println("DEBUG:" + action.toString());

        switch(action.getAction()) {
            case JOIN:
                joinAction(action);
                break;
            case LEAVE:
                leaveAction(action);
                break;
            default:
                break;
        }
    }

    private void leaveAction(ChatOperationProtos.ChatAction action) {
        String login = action.getNickname();
        String channel = action.getChannel();

        if(channels.containsKey(channel)) {
            channels.get(channel).remove(login);

            boolean userExists = false;
            for(Map.Entry<String, Set<String>> channelInfo : channels.entrySet()) {
                if(channelInfo.getValue().contains(login)) {
                    userExists = true;
                }
            }
            if(!userExists) {
                users.remove(login);
            }
        }
    }

    private void joinAction(ChatOperationProtos.ChatAction action) {
        String login = action.getNickname();
        String channel = action.getChannel();

        if(!channels.containsKey(channel)) {
            channels.put(channel, new HashSet<String>());
        }
        channels.get(channel).add(login);
        users.add(login);
    }

    public void showChannels() {
        for(Map.Entry<String, Set<String>> channelInfo : channels.entrySet()) {
            System.out.println("> " + channelInfo.getKey());
            for(String user : channelInfo.getValue()) {
                System.out.println(">> " + user);
            }
        }
    }

    public void createChannel(int number) throws Exception, ChannelExistsException, ChannelDoNotExistsException, ChannelJoinedException {
        String channelAddress = ADDRESS_PREFIX + number;

        if(this.channels.containsKey(channelAddress)) {
            throw new ChannelExistsException();
        }
        channels.put(channelAddress, new HashSet<String>());
        ChatOperationProtos.ChatAction message = ChatOperationProtos.ChatAction.newBuilder().setChannel(channelAddress).setNickname(this.login)
                .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN).build();
        managmentChannel.send(null, message.toByteArray());
        joinChannel(number);
    }

    public void joinChannel(int number) throws Exception, ChannelDoNotExistsException, ChannelJoinedException {
        String channelAddress = ADDRESS_PREFIX + number;

        if(!this.channels.containsKey(channelAddress)) {
            throw new ChannelDoNotExistsException();
        }

        if(this.joinedChannels.containsKey(channelAddress)) {
            throw new ChannelJoinedException();
        }

        JChannel channel = new JChannel(false);
        ProtocolStack stack = new ProtocolStack();
        StackConfigurator.configureStack(stack, channelAddress);
        channel.setProtocolStack(stack);
        channel.setName(this.login);
        stack.init();

        channels.get(channelAddress).add(this.login);
        channel.connect(channelAddress);
        channel.setReceiver(new ChatListener(channelAddress));
        channel.setDiscardOwnMessages(true);
        joinedChannels.put(channelAddress, channel);

        ChatOperationProtos.ChatAction message = ChatOperationProtos.ChatAction.newBuilder().setChannel(channelAddress).setNickname(this.login)
                .setAction(ChatOperationProtos.ChatAction.ActionType.JOIN).build();
        managmentChannel.send(null, message.toByteArray());
    }

    public void leaveChannel(int number) throws Exception, ChannelNotJoinedException {
        String channelAddress = ADDRESS_PREFIX + number;
        if(!this.joinedChannels.containsKey(channelAddress)) {
            throw new ChannelNotJoinedException();
        }

        joinedChannels.remove(channelAddress);
        channels.get(channelAddress).remove(this.login);

        ChatOperationProtos.ChatAction message = ChatOperationProtos.ChatAction.newBuilder().setChannel(channelAddress).setNickname(this.login)
                .setAction(ChatOperationProtos.ChatAction.ActionType.LEAVE).build();
        managmentChannel.send(null, message.toByteArray());
    }

    public void chatMessage(int number, String message) throws ChannelNotJoinedException, Exception {
        String channelAddress = ADDRESS_PREFIX + number;

        if(!this.joinedChannels.containsKey(channelAddress)) {
            throw new ChannelNotJoinedException();
        }

        ChatOperationProtos.ChatMessage chatMessage = ChatOperationProtos.ChatMessage.newBuilder().setMessage(message).build();
        this.joinedChannels.get(channelAddress).send(null, chatMessage.toByteArray());
    }

    public void close() {
        for(JChannel jchannel : this.joinedChannels.values()) {
            jchannel.close();
        }
        this.managmentChannel.close();
    }
}
