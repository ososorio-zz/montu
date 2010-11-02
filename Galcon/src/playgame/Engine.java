package playgame;

import java.io.*;
import java.util.*;

public class Engine
{

    public Engine()
    {
    }

    public static void KillClients(List<Process> list)
    {
        Iterator<Process> iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Process process = (Process)iterator.next();
            if(process != null)
                process.destroy();
        } while(true);
    }

    public static boolean AllTrue(boolean aflag[])
    {
        for(int i = 0; i < aflag.length; i++)
            if(!aflag[i])
                return false;

        return true;
    }

    public static void main(String args[])
    {
        if(args.length < 5)
        {
            System.err.println("ERROR: wrong number of command-line arguments.");
            System.err.println("USAGE: engine map_file_name max_turn_time max_num_turns log_filename player_one player_two [more_players]");
            System.exit(1);
        }
        String mapFileName = args[0];
        int maxTurnTime = Integer.parseInt(args[1]);
        int maxNumTurns = Integer.parseInt(args[2]);
        String logFileName = args[3];
        Game game = new Game(mapFileName, maxNumTurns, 0, logFileName);
        if(game.Init() == 0)
            System.err.println((new StringBuilder()).append("ERROR: failed to start game. map: ").append(mapFileName).toString());
//        long l = Integer.parseInt(args[1]);
        ArrayList<Process> playerBots = new ArrayList<Process>();
        for(int k = 4; k < args.length; k++)
        {
            String s2 = args[k];
            Process process = null;
            try
            {
                process = Runtime.getRuntime().exec(s2);
            }
            catch(Exception exception)
            {
                process = null;
            }
            if(process == null)
            {
                KillClients(playerBots);
                System.err.println((new StringBuilder()).append("ERROR: failed to start client: ").append(s2).toString());
                System.exit(1);
            }
            playerBots.add(process);
        }

        boolean aflag[] = new boolean[playerBots.size()];
        for(int i1 = 0; i1 < playerBots.size(); i1++)
            aflag[i1] = playerBots.get(i1) != null;

        int j1 = 0;
        for(; game.Winner() < 0; game.DoTimeStep())
        {
            for(int k1 = 0; k1 < playerBots.size(); k1++)
            {
                if(playerBots.get(k1) == null || !game.IsAlive(k1 + 1))
                    continue;
                String s3 = (new StringBuilder()).append(game.PovRepresentation(k1 + 1)).append("go\n").toString();
                try
                {
                    java.io.OutputStream outputstream = ((Process)playerBots.get(k1)).getOutputStream();
                    OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
                    outputstreamwriter.write(s3, 0, s3.length());
                    outputstreamwriter.flush();
                    game.WriteLogMessage((new StringBuilder()).append("engine > player").append(k1 + 1).append(": ").append(s3).toString());
                }
                catch(Exception exception1)
                {
                    playerBots.set(k1, null);
                }
            }

            StringBuilder astringbuilder[] = new StringBuilder[playerBots.size()];
            boolean aflag1[] = new boolean[playerBots.size()];
            for(int l1 = 0; l1 < playerBots.size(); l1++)
            {
                astringbuilder[l1] = new StringBuilder();
                aflag1[l1] = false;
            }

            for(long time = System.currentTimeMillis(); !AllTrue(aflag1) && System.currentTimeMillis() - time < (long)maxTurnTime;)
            {
                int i2 = 0;
                while(i2 < playerBots.size()) 
                {
                    if(!aflag[i2] || !game.IsAlive(i2 + 1) || aflag1[i2])
                        aflag1[i2] = true;
                    else
                        try
                        {
                            for(InputStream inputstream = ((Process)playerBots.get(i2)).getInputStream(); inputstream.available() > 0;)
                            {
                                char c = (char)inputstream.read();
                                if(c == '\n')
                                {
                                    String s4 = astringbuilder[i2].toString();
                                    s4 = s4.toLowerCase().trim();
                                    game.WriteLogMessage((new StringBuilder()).append("player").append(i2 + 1).append(" > engine: ").append(s4).toString());
                                    if(s4.equals("go"))
                                        aflag1[i2] = true;
                                    else
                                        game.IssueOrder(i2 + 1, s4);
                                    astringbuilder[i2] = new StringBuilder();
                                } else
                                {
                                    astringbuilder[i2].append(c);
                                }
                            }

                        }
                        catch(Exception exception2)
                        {
                            System.err.println((new StringBuilder()).append("WARNING: player ").append(i2 + 1).append(" crashed.").toString());
                            ((Process)playerBots.get(i2)).destroy();
                            game.DropPlayer(i2 + 1);
                            aflag[i2] = false;
                        }
                    i2++;
                }
            }

            for(int j2 = 0; j2 < playerBots.size(); j2++)
                if(aflag[j2] && game.IsAlive(j2 + 1) && !aflag1[j2])
                {
                    System.err.println((new StringBuilder()).append("WARNING: player ").append(j2 + 1).append(" timed out.").toString());
                    ((Process)playerBots.get(j2)).destroy();
                    game.DropPlayer(j2 + 1);
                    aflag[j2] = false;
                }

            j1++;
            System.err.println((new StringBuilder()).append("Turn ").append(j1).toString());
        }

        KillClients(playerBots);
        if(game.Winner() > 0)
            System.err.println((new StringBuilder()).append("Player ").append(game.Winner()).append(" Wins!").toString());
        else
            System.err.println("Draw!");
        System.out.println(game.GamePlaybackString());
    }
}
