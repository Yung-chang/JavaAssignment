import java.util.ArrayList;
import java.util.Scanner;

public class UserLoginProject {

    public static void main(String[] args) {
        // 使用 ArrayList 管理多個使用者物件 (專案目標：動態集合管理)
        ArrayList<User> userList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        // 初始化預設資料 (展現繼承與多型)
        userList.add(new StudentUser("Flame", "student01", "1234"));
        userList.add(new User("Admin User", "admin", "admin888"));

        System.out.println("=== 專案測試：使用者登入系統 ===");

        // 實作 try-catch-finally 例外處理架構
        try {
            System.out.print("請輸入帳號: ");
            String inputId = sc.nextLine();

            System.out.print("請輸入密碼: ");
            String inputPw = sc.nextLine();

            boolean loginSuccess = false;

            // 遍歷 ArrayList 進行比對
            for (User u : userList) {
                // 透過封裝好的方法進行驗證，不直接存取私有成員
                if (u.getUsername().equals(inputId) && u.checkPassword(inputPw)) {
                    System.out.println("\n[結果] 登入成功！");
                    u.showRole(); // 展現多型動態綁定
                    loginSuccess = true;
                    break;
                }
            }

            if (!loginSuccess) {
                System.out.println("\n[結果] 登入失敗：帳號或密碼錯誤。");
            }

        } catch (Exception e) {
            System.out.println("系統發生異常錯誤: " + e.getMessage());
        } finally {
            // 確保資源正確釋放
            sc.close();
            System.out.println("系統資源已釋放，程式關閉。");
        }
    }
}

// 基礎類別：定義通用屬性
class Person {
    protected String name; // 使用 protected 允許子類別存取

    public Person(String name) {
        this.name = name;
    }
}

// 使用者類別：落實封裝原則
class User extends Person {
    private String username; // 私有變數
    private String password;

    public User(String name, String username, String password) {
        super(name);
        this.username = username;
        this.password = password;
    }

    // 提供 Getter 存取帳號
    public String getUsername() {
        return username;
    }

    // 封裝比對邏輯，不暴露原始密碼
    public boolean checkPassword(String pw) {
        return this.password.equals(pw);
    }

    public void showRole() {
        System.out.println("使用者名稱: " + name + " | 權限: 一般使用者");
    }
}

// 學生使用者類別：實作方法覆寫與多型
class StudentUser extends User {
    public StudentUser(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    public void showRole() {
        System.out.println("使用者名稱: " + name + " | 權限: 學生使用者 (Student)");
    }
}