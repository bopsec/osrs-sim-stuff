import java.util.*;

class Scythetech {
  // my stats .
  int scytheMAR = 42936;
  int scytheMax = 54;
  int scytheSpeed = 5;

  int BlowpipeMAR = 39845;
  int BlowpipeMax = 35;
  int BlowpipeSpeed = 2;
  int BlowpipeTravelTime = 1;

  int TbowMAR = 64927;
  int TbowMax = 88;
  int TbowSpeed = 5;
  int TbowTravelTime = 1;

  // npc stats. --------------

  // BAT stats, 1x1
  int BatMDR = 1535;
  int BatHP = 10;
  int BatDeathAnim = 3;

  // Blob stats, 2x2
  int BlobMDR = 2496;
  int BlobHP = 20;
  int BlobDeathAnim = 5;

  // Ranger stats, 3x3
  int RangeMDR = 4416;
  int RangeHP = 40;
  int RangeDeathAnim = 5;

  // Meleer stats, 3x3
  int MeleeMDR = 8256;
  int MeleeHP = 80;
  int MeleeDeathAnim = 5;

  // Mager stats, 3x3
  int MageMDR = 15936;
  int MageHP = 160;
  int MageDeathAnim = 5;


  public int hp = 5000;
  public int killTime = 0;

  Random rand = new Random(System.nanoTime());

  public Scythetech(){
  }

  public double calcAccuracy(double MAR, double MDR) {
    double accuracy;

    if (MAR > MDR) {
      accuracy = 1 - (MDR + 2) / (2 * (MAR + 1));
    } else {
      accuracy = (MAR / (2 * (MDR + 1)));
    }
    return accuracy;
  }

  public void ScytheHit(int mob) {
   /*
    * 0 = bat,
    * 1 = blob,
    * 2 = ranger,
    * 3 = meleer,
    * 4 = mager
    */

    double accuracy;
    // Mob = Bat (1x1)
    if (mob == 0) {
      accuracy = calcAccuracy(scytheMAR, BatMDR);
      if (accuracy > Math.random()) {
        // First scythe hit.
        hp -= rand.nextInt(scytheMax + 1);
      }
    }
    // Mob = Big blob (2x2)
    if (mob == 1) {
      accuracy = calcAccuracy(scytheMAR, BlobMDR);
      if (accuracy > Math.random()) {
        // First scythe hit.
        hp -= rand.nextInt(scytheMax + 1);
        if (hp <= 0) {
          //Death animation speedup will not actually save a tick
          //killTime -= 1;
        }
      } if (accuracy > Math.random()) {
        // Second scythe hit
        hp -= rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
      }
    }

    // Mob = 3x3
    if (mob > 1) {
      // placeholder value for accuracy .
      accuracy = calcAccuracy(scytheMAR, RangeMDR);
      // Mob = ranger
      if (mob == 2) {
        accuracy = calcAccuracy(scytheMAR, RangeMDR);
      } else if (mob == 3) { // Mob = meleer
        accuracy = calcAccuracy(scytheMAR, MeleeMDR);
      } else if (mob == 4) { // Mob = mager
        accuracy = calcAccuracy(scytheMAR, MageMDR);
      }
      if (accuracy > Math.random()) { // First scythe hitsplat
        hp -= rand.nextInt(scytheMax + 1);
      } if (accuracy > Math.random()) { // Second scythe hitsplat
        hp -= rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
        if (hp <= 0) { // If it is dead at this point, death animation is sped up by one tick.
          killTime -= 1;
        }
      } if (accuracy > Math.random()) { // Third scythe hitsplat
        hp -= rand.nextInt((int)Math.floor(scytheMax / 4) + 1);
      }
    }
  }

  public void BlowpipeHit(int mob) {
    // 0 = bat, 1 = blob, 2 = ranger, 3 = meleer
    double accuracy = 0;
    if (mob == 0) { // bat
      accuracy = calcAccuracy(BlowpipeMAR, BatMDR);
    } if (mob == 1) { // blob
      accuracy = calcAccuracy(BlowpipeMAR, BlobMDR);
    } if (mob == 2) { // ranger
      accuracy = calcAccuracy(BlowpipeMAR, RangeMDR);
    } if (mob == 3 ) { // meleer
      accuracy = calcAccuracy(BlowpipeMAR, MeleeMDR);
    } if (mob == 4) { // mager
      accuracy = calcAccuracy(BlowpipeMAR, MageMDR);
    }
    if (accuracy > Math.random()) {
      hp -= rand.nextInt(BlowpipeMax + 1);
    }
  }

  public void Tbowhit() {
    double accuracy = calcAccuracy(TbowMAR, MageMDR);
    int hit = 0;
    if (accuracy > Math.random()) {
      hit = rand.nextInt(TbowMax + 1);
      hp -= hit;
    }
  }

  public int simulateKill(int hpForSwitch, int mob) {
    killTime = 0;

    if (mob == 0) {
      hp = 10;
    } if (mob == 1) {
      hp = 20;
    } if (mob == 2) {
      hp = 40;
    } if (mob == 3) {
      hp = 80;
    } if (mob == 4) {
      hp = 160;
    }

    while (hp > hpForSwitch) {
      BlowpipeHit(mob);
      killTime += BlowpipeSpeed;
      if (hp <= 0) {
        killTime -= BlowpipeSpeed;
        killTime += 1;
      }
    }
    while (hp > 0 && hp <= hpForSwitch) {
      ScytheHit(mob);
      killTime += scytheSpeed;
      if (hp <= 0) {
        killTime -= scytheSpeed;
        killTime += 0;
      }
    }
    return killTime;
  }

  public int simulateKill(int hpForSwitch, int mob, int hpForSwitchTbow) {
    killTime = 0;
    // i don't really know yet lol
    if (mob == 4) {
      hp = 160;
    }
    while (hp > hpForSwitchTbow && hp > hpForSwitch) {
      Tbowhit();
      killTime += TbowSpeed;
      if (hp <= 0) {
        killTime -= TbowSpeed;
        killTime += TbowTravelTime;
      }
    }
    while (hp > hpForSwitch) {
      BlowpipeHit(mob);
      killTime += BlowpipeSpeed;
      if (hp <= 0) {
        killTime -= BlowpipeSpeed;
        killTime += BlowpipeTravelTime;
      }
    }
    while (hp > 0 && hp <= hpForSwitch) {
      ScytheHit(mob);
      killTime += scytheSpeed;
      if (hp <= 0) {
        killTime -= scytheSpeed;
        killTime += 0;
      }
    }
    return killTime;
  }

  public static void main(String[] args) {
    int runs = 10000001;
    Scythetech st = new Scythetech();

    ArrayList<Integer> scythetechtimes = new ArrayList<Integer>();

    double bestTime = Integer.MAX_VALUE;
    int switchHp = 161;
    double totalTime = 0;
    // BAT
    for (int i = 5; i < st.BatHP + 1; i++) {
      scythetechtimes = new ArrayList<Integer>();
      for (int x = 0; x < runs; x++) {
        scythetechtimes.add(st.simulateKill(i,0));
      }
      totalTime = 0;
      for (double d: scythetechtimes) {
        totalTime += d;
      }
      if (totalTime < bestTime) {
        switchHp = i;
        bestTime = totalTime;
      }
    }
    System.out.println("Scythe tech bat hp to scythe: " + switchHp + ", average killtime " + (double)Math.round((bestTime / runs)*100)/100);
    System.out.println("Time saved: " + (double)Math.round((1.7573412426587574 - bestTime / runs)*100)/100);
    System.out.println();

    scythetechtimes = new ArrayList<Integer>();
    bestTime = Integer.MAX_VALUE;
    switchHp = 161;
    // BLOB
    for (int i = 9; i < st.BlobHP + 1; i++) {
      scythetechtimes = new ArrayList<Integer>();
      for (int x = 0; x < runs; x++) {
        scythetechtimes.add(st.simulateKill(i,1));
      }
      totalTime = 0;
      for (double d: scythetechtimes) {
        totalTime += d;
      }
      if (totalTime < bestTime) {
        switchHp = i;
        bestTime = totalTime;
      }
    }
    System.out.println("Scythe tech blob hp to scythe: " + switchHp + ", average killtime " + (double)Math.round((bestTime / runs)*100)/100);
    System.out.println("Time saved: " + (double)Math.round((2.773102226897773 - bestTime / runs)*100)/100);
    System.out.println();

    scythetechtimes = new ArrayList<Integer>();
    bestTime = Integer.MAX_VALUE;
    switchHp = 161;

    // RANGE
    for (int i = 25; i < st.RangeHP + 1; i++) {
      scythetechtimes = new ArrayList<Integer>();
      for (int x = 0; x < runs; x++) {
        scythetechtimes.add(st.simulateKill(i,2));
      }
      totalTime = 0;
      for (double d: scythetechtimes) {
        totalTime += d;
      }
      if (totalTime < bestTime) {
        bestTime = totalTime;
        switchHp = i;
      }
    }
    System.out.println("Scythe tech range hp to scythe: " + switchHp + ", average killtime " + (double)Math.round((bestTime / runs)*100)/100);
    System.out.println("Time saved: " + (double)Math.round((5.596445403554596 - bestTime / runs)*100)/100);
    System.out.println();

    scythetechtimes = new ArrayList<Integer>();
    bestTime = Integer.MAX_VALUE;
    switchHp = 161;
    // MELEE
    for (int i = 0; i < 80; i++) {
      scythetechtimes = new ArrayList<Integer>();
      for (int x = 0; x < runs; x++) {
        scythetechtimes.add(st.simulateKill(i,3));
      }
      totalTime = 0;
      for (double d: scythetechtimes) {
        totalTime += d;
      }
      if (totalTime < bestTime) {
        switchHp = i;
        bestTime = totalTime;
      }
    }

    System.out.println("Scythe tech melee hp to scythe: " + switchHp + ", average killtime " + (double)Math.round((bestTime / runs)*100)/100);
    System.out.println("Time saved: " + (double)Math.round((11.520807479192522 - bestTime / runs)*100)/100);
    System.out.println();

    int switchHpTbow = 161;
    scythetechtimes = new ArrayList<Integer>();
    bestTime = Integer.MAX_VALUE;
    switchHp = 161;
    // MAGE
    for (int i = 10; i < 160; i++) {
      for (int l = 0; l < 1; l++) {
        scythetechtimes = new ArrayList<Integer>();
        for (int x = 0; x < runs; x++) {
          scythetechtimes.add(st.simulateKill(i,4,l));
        }
        totalTime = 0;
        for (double d: scythetechtimes) {
          totalTime += d;
        }
        if (totalTime < bestTime) {
          switchHp = i;
          switchHpTbow = l;
          bestTime = totalTime;
        }
        //System.out.println("Average killtime l = " + l + ": " + totalTime / runs);
      }
    }
    System.out.println("Scythe tech mage hp to blowpipe: " + switchHpTbow + ", hp to scythe: " + switchHp);
    System.out.println("Average killtime: " + (double)Math.round((bestTime / runs)*100)/100);
    System.out.println("Time saved: " + (double)Math.round((22.002443997556004 - bestTime / runs)*100)/100);
  }
}
