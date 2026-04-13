import java.util.Random;
import java.util.Scanner;

public class teachworfassingment {

    // 1. Player 類別
    static class Player {
        private int id;
        private String role;
        private boolean alive;

        public Player(int id, String role) {
            this.id = id;
            this.role = role;
            this.alive = true;
        }

        public int getId() { return id; }
        public String getRole() { return role; }
        public boolean isAlive() { return alive; }
        public void kill() { this.alive = false; }

        public String getPublicInfo() {
            return "Player " + id + (alive ? " [Alive]" : " [Dead]");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.println("--- Welcome to WolfGame ---");
        System.out.println("Enter number of players (4~10): ");
        int n = sc.nextInt();
        sc.nextLine();

        while (n < 4 || n > 10) {
            System.out.println("Invalid number. Please enter a number between 4 and 10.");
            n = sc.nextInt();
            sc.nextLine();
        }

        // 初始化玩家與分配狼人
        Player[] players = new Player[n];
        int wolfIndex = rand.nextInt(n); 

        for (int i = 0; i < n; i++) {
            if (i == wolfIndex) {
                players[i] = new Player(i + 1, "Werewolf");
            } else {
                players[i] = new Player(i + 1, "Villager");
            }
        }

        // 角色確認階段
        System.out.println("\nGame Start! Role assignment...");
        for (int i = 0; i < n; i++) {
            System.out.println("Player " + (i + 1) + ", press Enter to see your role.");
            sc.nextLine();
            System.out.println("Your Role : " + players[i].getRole());
            System.out.println("Memorize your role, then press Enter to clear.");
            sc.nextLine();
            for (int line = 0; line < 30; line++) System.out.println();
        }

        boolean gameOver = false;
        int round = 1;

        // 主遊戲迴圈
        while (!gameOver) {
            System.out.println("\n==== Round " + round + " ====");
            
            // --- 夜晚階段 (狼人殺人) ---
            System.out.println("Night falls. Werewolf wakes up.");
            int wolfIdx = findAliveWerewolf(players);
            
            if (wolfIdx != -1) {
                printAlivePlayers(players);
                System.out.print("Werewolf (Player " + (wolfIdx + 1) + "), choose a Player ID to kill: ");
                int targetId = sc.nextInt();
                sc.nextLine();

                if (targetId >= 1 && targetId <= n && players[targetId - 1].isAlive()) {
                    players[targetId - 1].kill();
                    System.out.println("\n[System] Night results: Player " + targetId + " was killed.");
                } else {
                    System.out.println("\n[System] Invalid target. No one was killed tonight.");
                }
            }

            if (checkGameOver(players)) break;

            // --- 白天階段 (大眾投票) ---
            System.out.println("\nDaytime. Discussion starts.");
            printAlivePlayers(players);
            System.out.print("All players, vote to execute a Player ID: ");
            int voteId = sc.nextInt();
            sc.nextLine();

            if (voteId >= 1 && voteId <= n && players[voteId - 1].isAlive()) {
                players[voteId - 1].kill();
                System.out.println("\n[System] Player " + voteId + " was executed by the village.");
            } else {
                System.out.println("\n[System] Invalid vote. No one was executed.");
            }

            if (checkGameOver(players)) break;
            
            round++;
        }
        System.out.println("\n--- Game Over ---");
    }

    // --- 輔助方法 (必須放在 main 之外) ---

    public static int findAliveWerewolf(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isAlive() && players[i].getRole().equals("Werewolf")) {
                return i;
            }
        }
        return -1;
    }

    public static void printAlivePlayers(Player[] players) {
        System.out.println("Current alive players:");
        for (Player p : players) {
            if (p.isAlive()) System.out.println(p.getPublicInfo());
        }
    }

    public static boolean checkGameOver(Player[] players) {
        int wolfCount = 0;
        int villagerCount = 0;

        for (Player p : players) {
            if (p.isAlive()) {
                if (p.getRole().equals("Werewolf")) wolfCount++;
                else villagerCount++;
            }
        }

        if (wolfCount == 0) {
            System.out.println("\n[Result] Villagers Win! All werewolves are dead.");
            return true;
        } else if (wolfCount >= villagerCount) {
            System.out.println("\n[Result] Werewolves Win! They have outnumbered the villagers.");
            return true;
        }
        return false;
    }
}