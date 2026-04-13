import java.util.*;

public class InteractiveDS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 選擇資料結構
        System.out.println("Select Data Structure:\n1. Stack (LIFO)\n2. Queue (FIFO)");
        int type = Integer.parseInt(scanner.nextLine());
        
        // 宣告結構
        Stack<Integer> stack = new Stack<>();
        Queue<Integer> queue = new LinkedList<>();
        
        boolean running = true;
        while (running) {
            System.out.println("\n--------------------------------");
            System.out.println("Current Status: " + (type == 1 ? stack : queue));
            System.out.println("Actions: 1. Put (Add) | 2. Get (Remove) | 3. Exit");
            System.out.print("Choice: ");
            
            String action = scanner.nextLine();
            
            switch (action) {
                case "1": // 放入元素
                    System.out.print("Enter a number to put: ");
                    int num = Integer.parseInt(scanner.nextLine());
                    if (type == 1) {
                        stack.push(num);
                        System.out.printf("[Step] Push %d to Stack Top | Complexity: O(1)\n", num);
                    } else {
                        queue.offer(num);
                        System.out.printf("[Step] Offer %d to Queue Tail | Complexity: O(1)\n", num);
                    }
                    break;
                    
                case "2": // 取出元素
                    if (type == 1) {
                        if (stack.isEmpty()) {
                            System.out.println("Stack is empty! Cannot pop.");
                        } else {
                            int val = stack.pop();
                            System.out.printf("[Step] Pop %d from Stack Top | Complexity: O(1)\n", val);
                        }
                    } else {
                        if (queue.isEmpty()) {
                            System.out.println("Queue is empty! Cannot poll.");
                        } else {
                            int val = queue.poll();
                            System.out.printf("[Step] Poll %d from Queue Head | Complexity: O(1)\n", val);
                        }
                    }
                    break;
                    
                case "3":
                    running = false;
                    System.out.println("Exiting Demo.");
                    break;
                    
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}