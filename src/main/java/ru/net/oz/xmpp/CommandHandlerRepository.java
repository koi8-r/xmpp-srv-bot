package ru.net.oz.xmpp;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlerRepository {

    private Map<String,ICommandHandler> db = new HashMap<>() ;

    private static CommandHandlerRepository INSTANCE ;

    //private static final CommandHandlerRepository INSTANCE = new CommandHandlerRepository() ;
    //public static final CommandHandlerRepository getInstance() {
    //    return INSTANCE ;
    //}

    public static CommandHandlerRepository getInstance() {
        if(INSTANCE != null)
            return INSTANCE ;
        else
            return createInstance() ;
    }

    private static synchronized CommandHandlerRepository createInstance() {
        if(INSTANCE == null)
            INSTANCE = new CommandHandlerRepository() ;

        return INSTANCE ;
    }


    public void register(String cmd, ICommandHandler handler) {
        if(this.db.containsKey(cmd))
            throw new IllegalArgumentException(  String.format("Handler for command - '%s' already exists", handler)  ) ;

        this.db.put(cmd, handler) ;
    }

    public String handle(String cmd, String args) throws NotImplementedEx {
        ICommandHandler handler = this.db.get(cmd) ;

        if(handler == null)
            throw new NotImplementedEx(cmd + " not implemented.") ;

        // TODO: Implement args
        return handler.execute(null) ;
    }

}
