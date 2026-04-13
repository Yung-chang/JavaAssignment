import java.util.*;

public class InteractiveDataDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Interactive Stack & Queue Demo ===");
        System.out.print("Enter numbers separated by spaces (e.g., 10 20 30): ");
        
        String input = scanner.nextLine();
        String[] parts = input.split("\\s+");
        List<Integer> numList = new ArrayList<>();
        
        for (String s : parts) {
            try {
                numList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid input: " + s);
            }
        }

        if (numList.isEmpty()) {
            System.out.println("No valid numbers entered. Exiting.");
            return;
        }

        // --- Stack Section ---
        System.out.println("\n--- [STACK OPERATION: LIFO] ---");
        Stack<Integer> stack = new Stack<>();
        for (int n : numList) {
            stack.push(n);
            System.out.printf("PUSH %d | Current Stack: %-15s | Complexity: O(1)\n", n, stack);
        }
        System.out.println("PEEK    | Top Element: " + stack.peek() + "          | Complexity: O(1)");
        while (!stack.isEmpty()) {
            int val = stack.pop();
            System.out.printf("POP  %d | Remaining Stack: %-15s | Complexity: O(1)\n", val, stack);
        }

        // --- Queue Section ---
        System.out.println("\n--- [QUEUE OPERATION: FIFO] ---");
        Queue<Integer> queue = new LinkedList<>();
        for (int n : numList) {
            queue.offer(n);
            System.out.printf("OFFER %d | Current Queue: %-15s | Complexity: O(1)\n", n, queue);
        }
        System.out.println("PEEK     | Front Element: " + queue.peek() + "        | Complexity: O(1)");
        while (!queue.isEmpty()) {
            int val = queue.poll();
            System.out.printf("POLL  %d | Remaining Queue: %-15s | Complexity: O(1)\n", val, queue);
        }

        scanner.close();
    }
}