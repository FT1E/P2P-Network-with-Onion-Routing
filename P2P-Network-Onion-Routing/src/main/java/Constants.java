public class Constants {

    private static final int SERVER_PORT = 9000;

    private static int BOOTSTRAP_IP;

    public static int getSERVER_PORT() {
        return SERVER_PORT;
    }


    public static int getBOOTSTRAP_IP() {
        return BOOTSTRAP_IP;
    }

    public static void setBOOTSTRAP_IP(int bootstrapIp) {
        Constants.BOOTSTRAP_IP = bootstrapIp;
    }

}
