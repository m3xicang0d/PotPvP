package net.frozenorb.potpvp.player.elo;

import lombok.Getter;

public final class EloCalculator {

    public final double kPower;
    public final int minEloGain;
    public final int maxEloGain;
    public final int minEloLoss;
    public final int maxEloLoss;

    public EloCalculator(double kPower, int minEloGain, int maxEloGain, int minEloLoss, int maxEloLoss) {
        this.kPower = kPower;
        this.minEloGain = minEloGain;
        this.maxEloGain = maxEloGain;
        this.minEloLoss = minEloLoss;
        this.maxEloLoss = maxEloLoss;
    }

    public Result calculate(int winnerElo, int loserElo) {
        double winnerQ = Math.pow(10, ((double) winnerElo) / 300D);
        double loserQ = Math.pow(10, ((double) loserElo) / 300D);

        double winnerE = winnerQ / (winnerQ + loserQ);
        double loserE = loserQ / (winnerQ + loserQ);

        int winnerGain = (int) (kPower * (1 - winnerE));
        int loserGain = (int) (kPower * (0 - loserE));

        winnerGain = Math.min(winnerGain, maxEloGain);
        winnerGain = Math.max(winnerGain, minEloGain);

        // loserGain will be negative so pay close attention here
        loserGain = Math.min(loserGain, -minEloLoss);
        loserGain = Math.max(loserGain, -maxEloLoss);

        return new Result(winnerElo, winnerGain, loserElo, loserGain);
    }

    public static class Result {

        @Getter public final int winnerOld;
        @Getter public final int winnerGain;
        @Getter public final int winnerNew;

        @Getter public final int loserOld;
        @Getter public final int loserGain;
        @Getter public final int loserNew;

        Result(int winnerOld, int winnerGain, int loserOld, int loserGain) {
            this.winnerOld = winnerOld;
            this.winnerGain = winnerGain;
            this.winnerNew = winnerOld + winnerGain;

            this.loserOld = loserOld;
            this.loserGain = loserGain;
            this.loserNew = loserOld + loserGain;
        }

    }

}