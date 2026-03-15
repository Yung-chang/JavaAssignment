import java.util.Random;
import java.util.Scanner;

public class teachworfassingment {
    static class Player {
        
        private int id;
        private  String role;
        private boolean alive;
        // private id ,role,alive 

        public Player(){
            this.id = id;
            this.role = role;
            this.alive = true;

        }
        
        public int getId(){
            return id;
        }

        public String getRole(){
            return role;
        }

        public boolean isAlive(){
            return alive;
        }

        public void kill(){
            this.alive = false;
        }

        public String getPublicInfo(){
            if(alive){
                return "Player " + id + "[Alive]";
            }else{
                return "Player " + id + "[Dead]";                
            }
        }
        public static void main(String[] args){

            Scanner sc = new Scanner(System.in);
            Random rand = new Random();

            System.out.println("wolfGame");
            System.out.println("Enter number of playres (4~10) ");
            int n = sc.nextInt();
            sc.nextLine();

            while (n < 4 || n > 10 ) {
                System.out.println("Invalidnumber of players. Please enter a number between 4 and 10.");
                n = sc.nextInt();
                sc.nextLine();
            }
            Player[] players = new Players[n];
            int wolfIndex = rand.nextInt();
            
            for(int i = 0; i < n ; i++){
                if(i == wolfIndex){
                    players[i] = new Player(i+1, role = "WereWolf");
                }else{
                    players[i] = new Player(i+1, role = "Villager");
                }
        }


                System.out.println("Game Start!");
                System.out.print("Role assignment start.");
                System.out.print("Each player take turn to role.");

            for(int i = 0 ; i < n ; i++){
                System.out.println();
                System.out.println("Player " + (i + 1) + " Please Enter");
                sc.nextLine();
                System.out.println("Your Role : " + players[i].getRole());
                System.out.print("Memorize your role, then turn.");
                sc.nextLine();
                for(int line = 0; line < 30; line++){
                    System.out.println();
                }
            }

            boolean gameOver = false;
            int round = 1;

            while(!gameOver){
                System.out.println("Round " + round);
                System.out.println();

                System.out.println("Night falls. Werewolf wakes up.");
                int aliveWolf = findAliveWerewolf(players);
                if(aliveWolf != -1){
                    System.out.println("Werewolf is your turn.");
                    System.out.println("Alive players ");
                    printAlivePlayers(players);

                    int target = -1;

                    while(true){
                        System.out.println("Choose a player to kill ");
                        if(sc.hasNext()){
                            targetId = sc.nextInt();
                            sc.nextLine();
                            if(isValidTarget(targetId, players[aliveWolf]).getId()){         
                                break;
                            }else{
                                System.out.println("Invalid target.");
                                sc.nextLine();
                            }
                        }

                    }
                    players[targetId - 1].kill();
                    System.out.println("Night results: Player " + targetId + " has been killed.");
                }else{
                    System.out.println("No werewolf alive. Skip");
                }
            }

            if(checkkillvillagerwin(players)){
                System.out.println();
                System.out.println("Villagers wint! All werewolf die");
                gameOver = true;
            }else if(checkkillwerewolfwin(players)){
                System.out.println();
                System.out.println("Werewolf wins! Villagers have die");
                gameOver = true;
            }

            if(gameOver){
                break;
            }

            int voteId = -1;
            
            public static int printAliveWerewolf(Player[] players){
                for(int i = 0; i < players.length; i++){
                    if(players[i].isAlive() && players[i].getRole().equalds("Werewolf")){
                        return i;
                    }
                }
                return -1;
            }
            
            public static void printAlivePlayers(Player[] players){
                for(int i = 0; i < players.length; i++){
                    System.out.println(players[i].getPublicInfo());
                }
            }


        }


    }









}
