import java.util.*;

public class DataStructureApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n======= Data Structure Simulator =======");
            System.out.println("1. Demonstrate Stack (LIFO)");
            System.out.println("2. Demonstrate Queue (FIFO)");
            System.out.println("3. Exit");
            System.out.print("Select an option (1-3): ");
            
            String choice = scanner.nextLine();
            
            if (choice.equals("3")) {
                System.out.println("Exiting... Goodbye!");
                break;
            }
            
            System.out.print("Enter numbers separated by spaces (e.g., 5 10 15): ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            List<Integer> nums = new ArrayList<>();
            
            for (String p : parts) {
                try {
                    nums.add(Integer.parseInt(p));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid input: " + p);
                }
            }
            
            if (nums.isEmpty()) continue;

            if (choice.equals("1")) {
                runStackDemo(nums);
            } else if (choice.equals("2")) {
                runQueueDemo(nums);
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    private static void runStackDemo(List<Integer> nums) {
        System.out.println("\n--- [STACK: Last-In-First-Out] ---");
        Stack<Integer> stack = new Stack<>();
        
        // PUSH steps
        for (int n : nums) {
            stack.push(n);
            System.out.printf("Step (Push %d): Stack = %-15s | Complexity: O(1)\n", n, stack);
        }
        
        System.out.printf("Step (Peek): Top is %d              | Complexity: O(1)\n", stack.peek());
        
        // POP steps
        while (!stack.isEmpty()) {
            int val = stack.pop();
            System.out.printf("Step (Pop  %d): Stack = %-15s | Complexity: O(1)\n", val, stack);
        }
    }

    private static void runQueueDemo(List<Integer> nums) {
        System.out.println("\n--- [QUEUE: First-In-First-Out] ---");
        Queue<Integer> queue = new LinkedList<>();
        
        // OFFER steps
        for (int n : nums) {
            queue.offer(n);
            System.out.printf("Step (Offer %d): Queue = %-15s | Complexity: O(1)\n", n, queue);
        }
        
        System.out.printf("Step (Peek): Front is %d             | Complexity: O(1)\n", queue.peek());
        
        // POLL steps
        while (!queue.isEmpty()) {
            int val = queue.poll();
            System.out.printf("Step (Poll  %d): Queue = %-15s | Complexity: O(1)\n", val, queue);
        }
    }
}
