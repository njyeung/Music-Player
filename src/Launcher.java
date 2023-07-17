
public class Launcher {
    public static void main(String[] args) throws Exception {
        String bruh = "c: && cd ";

        // Find the path to the Muply directory
        // C:\Users\nickj\Desktop\Muply\Muply
        String path = System.getProperty("user.dir");
        System.out.println(path);
        bruh += path;

        // Find where java.exe is located
        // C:\Program Files\Java\jdk-
        String javaPath = System.getProperty("java.home");
        System.out.println(javaPath);
        bruh += " && cmd /C \"\""+javaPath+"\\bin\\java.exe\" -cp \""+path+"\\bin;"+path+"\\lib\\jansi-2.4.0.jar\" App \"";
        System.out.println(bruh);


        // Bruh should look like this:
        //c: && cd c:\Users\nickj\Desktop\Muply\Muply && cmd /C ""C:\Program Files\Java\jdk-17.0.4.1\bin\java.exe" -cp "C:\\Users\\nickj\\Desktop\\Muply\\Muply\\bin;c:\\Users\\nickj\\Desktop\\Muply\\Muply\\lib\\jansi-2.4.0.jar" App "

        String[] command = {"cmd.exe" , "/c", "start" , "cmd.exe", bruh};
        ProcessBuilder probuilder = new ProcessBuilder( command );
        probuilder.start();
    }
}
