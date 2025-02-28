package Model;

public class Default {
    public static class Config {
        public static int BOARD_SIZE = 19; // Giá trị mặc định ban đầu
        public static int TIME_PER_TURN = 30;
        public static double KOMI = 6.5;
        public static String SCORING = "Trung Quốc";
        public static String AI_DIFFICULTY = "Trung bình";
        public static String PLAYER_SIDE = "Đen";
    }

    public static void updateSettings(int boardSize, int timePerTurn, double komi, String scoring) {
        Config.BOARD_SIZE = boardSize;
        Config.TIME_PER_TURN = timePerTurn;
        Config.KOMI = komi;
        Config.SCORING = scoring;
    }

    public static void updateAISettings(String difficulty, String playerSide) {
        Config.AI_DIFFICULTY = difficulty;
        Config.PLAYER_SIDE = playerSide;
    }
}