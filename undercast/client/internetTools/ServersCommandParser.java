package undercast.client.internetTools;

import net.minecraft.client.Minecraft;
import undercast.client.UndercastCustomMethods;
import undercast.client.UndercastData;
import undercast.client.UndercastData.MatchState;

public class ServersCommandParser {

    private static boolean isListening = false;
    //is set to true if the mod casts /servers in order to delete the messages
    public static boolean castedByMod = false;
    public static boolean nextCastedByMod = true;
    public static int pages = 0;

    public static boolean handleChatMessage(String message, String unstripedMessage) {
        if (isListening) {
            // check if the message belongs to the command
            boolean commandEnded = false;
            if (!message.contains("Online: ") && !message.contains("--------- Overcast Network Servers")) {
                isListening = false;
                castedByMod = nextCastedByMod;
                UndercastCustomMethods.sortServers();
                commandEnded = true;
                UndercastData.removeNextChatMessage = false;
            }

            // don't try to handle it if the command ended
            if (!commandEnded) {
                //our only interest is the MatchState
                if (message.contains("Current Map: ")) {
                    String name;
                    String map;
                    char matchStatusColor;
                    MatchState state;

                    name = message.substring(0, message.indexOf(": "));
                    map = message.substring(message.indexOf("Current Map: ") + 13);
                    matchStatusColor = unstripedMessage.charAt(unstripedMessage.indexOf("Current Map§f: ") + 16);

                    //c == red
                    //e == yellow
                    //a = green
                    switch (matchStatusColor) {
                        case 'a':
                            state = MatchState.Starting;
                            break;
                        case 'c':
                            state = MatchState.Finished;
                            break;
                        case 'e':
                            state = MatchState.Started;
                            break;
                        default:
                            state = MatchState.Unknown;
                    }

                    //insert the data
                    for (int c = 0; c < UndercastData.serverInformation.length; c++) {
                        if (!(UndercastData.serverInformation[c].name == null)) {
                            if (UndercastData.serverInformation[c].name.equals(name)) {
                                UndercastData.serverInformation[c].currentMap = map;
                                UndercastData.serverInformation[c].matchState = state;
                            }
                        }
                    }
                }
            }
        }

        if (!isListening && message.contains("--------- Overcast Network Servers (1 of ")) {
            // get the page count
            try {
                pages = Integer.parseInt(message.substring(message.indexOf("of ") + 3, message.indexOf("of ") + 4));
            } catch (Exception e) {
                pages = 3;
            }
            // get the other pages
            for (int i = 2; i <= pages; i++) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/servers " + i);
            }

            if (castedByMod) {
                isListening = true;
            }
        }
        return UndercastData.removeNextChatMessage;
    }

    public static void castByMod() {
        if (!isListening) {
            castedByMod = true;
        } else {
            nextCastedByMod = true;
        }
    }
}