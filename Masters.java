import java.util.*;
// for fangsie and / or petcord


class Masters {
  int droprate = 1000;
  int killCount = 0;
  double totalHours = 0;

  boolean hasBloodhound = false;

  int passiveClues = 1212;
  double zerotime = 0;
  double zeroTimeCluesPerHour = 4;
  double notZerotime = 0;
  double notZeroTimeCluesPerHour = 1.56;

  Random rand = new Random(System.nanoTime());

  Masters() {
  }

  public void getBloodhound() {
    while (!hasBloodhound) {
      killCount ++;
      if (killCount < passiveClues) {
        zerotime ++;
      } else {
        notZerotime ++;
      }
      int roll = rand.nextInt(droprate + 1);
      if (roll == droprate) {
        hasBloodhound = true;
      }
      totalHours = zerotime / zeroTimeCluesPerHour + notZerotime / notZeroTimeCluesPerHour;
    }
  }

  public static void main(String[] args) {
    int completions;
    int runs = 1000000;
    ArrayList<Double> completionTime = new ArrayList<Double>();
    ArrayList<Integer> completionKC = new ArrayList<Integer>();
    Masters masters;
    for (int i = 0; i < runs; i++) {
      masters = new Masters();
      masters.getBloodhound();
      completionTime.add(masters.totalHours);
      completionKC.add(masters.killCount);
    }
    Collections.sort(completionKC);
    long totalKills = 0;
    long totalHours = 0;
    for (int i = 0; i < runs; i++) {
      totalKills += completionKC.get(i);
      totalHours += completionTime.get(i);
    }
    double averageKills = (double) totalKills / (double) runs;
    double averageHours = (double) totalHours / (double) runs;
    System.out.println("Average KC: " + averageKills);
    System.out.println("Average hours: " + averageHours);
    System.out.println("Lowest kc: " + completionKC.get(0));
    System.out.println("Highest kc: " + completionKC.get(runs - 1));
  }
}
