package ru.net.oz.xmpp ;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import ru.net.oz.xmpp.ru.net.oz.xmpp.commands.PingCommand;
import java.util.Properties;


public class App {
    public static void main( String[] args ) throws Exception {

        Properties props = new Properties() ;
        props.load( App.class.getClassLoader().getResourceAsStream("xmpp.properties") ) ;

        // TODO: Reflection - save all classes in package to META-INF/some-file
        CommandHandlerRepository cmdRepo = CommandHandlerRepository.getInstance() ;
        cmdRepo.register( "PING", new PingCommand() ) ;

        AbstractXMPPConnection con ;

        con = new XMPPTCPConnection(
                XMPPTCPConnectionConfiguration.builder()
                  .setDebuggerEnabled(false)
                  .setServiceName( props.getProperty("xmpp.service") )
                  .setHost( props.getProperty("xmpp.host") )
                  .setPort(5222)
                  .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                  //.setSocketFactory(SSLSocketFactory.getDefault())
                  .build()
        ) ;

        Roster.getInstanceFor(con).setRosterLoadedAtLogin(false) ;
        ReconnectionManager.getInstanceFor(con).enableAutomaticReconnection() ;

        con.connect() ;
        con.login(props.getProperty("xmpp.user"), props.getProperty("xmpp.password")) ;

        Presence presence = new Presence(Presence.Type.available) ;
        presence.setMode(Presence.Mode.available) ;
        presence.setStatus(".") ;

        PacketCollector acc = con.createPacketCollectorAndSend(  new StanzaTypeFilter(Message.class), presence  ) ;
        boolean end = false ;

        while(!end) {

            Stanza p = acc.nextResultBlockForever() ;
            Message msg = (Message) p ;

            if(msg.getBody() != null) {
                String resultMsg ;

                if( msg.getBody().startsWith("/") ) {

                    String cmd = msg.getBody().substring(1) ;

                    if(cmd.toUpperCase().equals("QUIT")) {
                        end = true ;
                        resultMsg = "Exiting" ;
                    } else {
                        try {
                            resultMsg = cmdRepo.handle(cmd, null) ;
                        } catch (NotImplementedEx ex) {
                            resultMsg = ex.getMessage() ;
                        }
                    }

                } else {
                    resultMsg = processChatMessage( msg.getBody() ) ;
                }

                con.sendStanza( new Message(msg.getFrom(), resultMsg) ) ;
            }

        } // while

        con.disconnect() ;
    }

    private static String processChatMessage(String msg) {
        return msg ;
    }

}
