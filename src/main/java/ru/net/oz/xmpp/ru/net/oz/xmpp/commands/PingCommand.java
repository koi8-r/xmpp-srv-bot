package ru.net.oz.xmpp.ru.net.oz.xmpp.commands;

import ru.net.oz.xmpp.ICommandHandler;

public class PingCommand implements ICommandHandler {
    @Override
    public String execute(String[] args) {
        return "pong" ;
    }
}
