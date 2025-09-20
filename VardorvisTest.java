import java.util.*;

public class VardorvisTest {
  int weapon;
  int spec;

  int bfCharges = 0;
  int scytheCharges = 0;

  int voidwakerMax = 76;
  int scytheMax = 48;
  int clawMax = 38;
  int[] soulreaperMax = new int[]{59, 62, 65, 67, 70, 73};
  int currentStack = 4;
  int totalBurnDamage = 0;
  int specTotalDamage = 0;

  int scytheAcc = 233; // full oathplate with bellator
  int soulreaperAcc = 242; // full oathplate with bellator
  int clawAcc = 161; // full oathplate with bellator

  int scytheSpeed = 5;
  int soulreaperSpeed = 5;
  int clawSpeed = 4;

  int defLvl = 215;
  int stabDef; // Ignored.
  int slashDef = 65;
  int crushDef; // Ignored.

  public int hp = 700;

  double MAR_stab, MAR_slash, MAR_crush;
  double MDR_stab, MDR_slash, MDR_crush;

  double accuracy;
  double killTime = 0.0;
  int weaponcase;
  int specBar = 100;

  Random rand = new Random();

  public VardorvisTest(int weaponcase) {
    this.weaponcase = weaponcase;
  }

  public double findMaxAttackRoll(int weapon) {
    if (weapon == 0) {
      // Weapon scythe, aggressive slash.
      MAR_slash = 43806; 
      return MAR_slash;
    } else if (weapon == 1) {
      // Weapon soulreaper, aggressive slash.
      MAR_slash = 45147;
      return MAR_slash;
    } else if (weapon == 2) {
      // Weapon claws, aggressive slash
      MAR_slash = 33078;
      return MAR_slash;
    }
    return -1;
  }

  public double findMaxDefRoll() {
      MDR_slash = Math.floor((defLvl + 9) * (slashDef + 64));
      return MDR_slash;
  }

  public double calcAccuracy(int atkStyle) {
    double MAR = 0;
    double MDR = 0;
    // 0 = stab, 1 = slash, 2 = crush
    if (atkStyle == 0) {
      MAR = MAR_stab;
      MDR = MDR_stab;
    } else if (atkStyle == 1) {
      MAR = MAR_slash;
      MDR = MDR_slash;
    } else if (atkStyle == 2) {
      MAR = MAR_crush;
      MDR = MDR_crush;
    }

    if (MAR > MDR) {
      accuracy = (1 - ((MDR + 2) / (2 * (MAR + 1))));
    } else {
      accuracy = (MAR / (2 * (MDR + 1)));
    }
    return accuracy;
  }

  public void soulreaperAxeHit(double accuracy) {
    int max = soulreaperMax[currentStack];
    int hit = 0;
    if (currentStack < 5) {
      currentStack++;
    }
    if (accuracy > Math.random()) {
      hit = rand.nextInt((int)max + 1);
    }
    hp -= hit;
  }

  public int clawSpec() {
    findMaxAttackRoll(2);
    findMaxDefRoll();
    double accuracy = calcAccuracy(1);
    double totalDamage = 0;

    // First accuracy roll
    int burnDamage = 0;
    double burnChance = 0.15;
    if (accuracy > Math.random()) {
        totalDamage = randomHit(Math.floor(0.75 * clawMax), Math.floor(1.75 * clawMax) + 1);
        int[] hits = new int[]{ (int)Math.floor(0.25 * totalDamage), (int)Math.floor(0.25 * totalDamage), (int)Math.floor(0.5 * totalDamage) };
        for (int i = 0; i < 3; i++) {
            hp -= hits[i];
            if (Math.random() < burnChance) {
                burnDamage += 10;
            }
            burnChance = (i == 0) ? 0.3 : 0.45; // Update burn chance for subsequent hits
        }
    } else {
        // Second accuracy roll
        if (accuracy > Math.random()) {
            totalDamage = randomHit(Math.floor(0.5 * clawMax), Math.floor(1.5 * clawMax) + 1);
            int[] hits = new int[]{ (int)Math.floor(0.5 * totalDamage) - 1, (int)Math.floor(0.5 * totalDamage) - 1, (int)totalDamage - ((int)Math.floor(0.5 * totalDamage) - 1) * 2 };
            burnChance = 0.3;
            for (int i = 0; i < 3; i++) {
                hp -= hits[i];
                if (Math.random() < burnChance) {
                    burnDamage += 10;
                }
                burnChance = (i == 0) ? 0.45 : 0.45; // Update burn chance for subsequent hits (remains the same)
            }
        } else {
            // Third accuracy roll
            if (accuracy > Math.random()) {
                totalDamage = randomHit(Math.floor(0.25 * clawMax), Math.floor(1.25 * clawMax) + 1);
                int[] hits = new int[]{ 1, 1, (int)totalDamage - 2 };
                burnChance = 0.45;
                for (int i = 0; i < 3; i++) {
                    hp -= hits[i];
                    if (Math.random() < burnChance) {
                        burnDamage += 10;
                    }
                }
            } else {
                // All accuracy rolls miss
                double roll = Math.random();
                if (roll < 0.2) {
                    // 0 damage
                } else if (roll < 0.6) {
                    hp -= 1;
                } else {
                    hp -= 2;
                }
            }
        }
    }
    specTotalDamage += totalDamage;
    return burnDamage;
  }

  public int voidwakerSpec() {
    int vwSpec = randomHit((int) voidwakerMax / 2, (int) voidwakerMax * 1.5);
    specTotalDamage += vwSpec;
    return vwSpec;
  }

  public int randomHit(double lower, double upper) {
    int hit = (int)(rand.nextInt((int)upper - (int)lower) + lower);
    return hit;
  }

  public void scytheSwing(double accuracy) {
    int hit = 0;
    scytheCharges ++;
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)scytheMax + 1);
    }
    if (hp - hit <= 0) {
      killTime --;
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
    }
    hp -= hit;
  }

  public void simulateKill() {
    int currentTick = 0;
    specBar = 100;
    if (weaponcase == 1) { // scythe camp
      hp = 700;
      while (hp > 0) {
        defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
        currentTick ++;
        if (currentTick % 5 == 0) {
            findMaxAttackRoll(0);
            findMaxDefRoll();
            accuracy  = calcAccuracy(1);
            scytheSwing(accuracy);
        }
      }
    } else if (weaponcase == 2) { // soulreaper camp
      hp = 700;
      while (hp > 0) {
          defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
           currentTick ++;
           if (currentTick % 5 == 0) {
                 findMaxAttackRoll(1);
                 findMaxDefRoll();
                 accuracy = calcAccuracy(1);
                 soulreaperAxeHit(accuracy);
          }
      }
    } else if (weaponcase == 3) { // Scythe with Burning Claws
        int burnDmg = 0;
        int burnTicks = 0;
        int burnDamagePerTick = 0;
        hp = 700;
        while (specBar >= 30) {
            int newBurnDamage = clawSpec();
            burnDmg += newBurnDamage;
            if (newBurnDamage > 0 && burnTicks == 0) {
                burnTicks = 10; // 40 ticks / 4 = 10 "damage instances"
                burnDamagePerTick = burnDmg / burnTicks;
            }
            specBar -= 30;
            killTime += 4;
        }
        defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
        currentTick = 0;
        while (hp > 0) {
            defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
            currentTick ++;
            if (currentTick % 5 == 0) {
              if ((currentTick + killTime > 103) && (specBar == 10)) {
                int newBurnDamage = clawSpec();
                burnDmg += newBurnDamage;
                if ((newBurnDamage > 0) && (burnTicks == 0)) {
                  burnTicks = 10;
                  burnDamagePerTick = burnDmg / burnTicks;
                }
                killTime --;
                specBar = 0;
              } else {
                findMaxAttackRoll(0);
                findMaxDefRoll();
                accuracy  = calcAccuracy(1);
                scytheSwing(accuracy);
              }
            }
            if (currentTick % 4 == 0 && burnDmg > 0) {
                int damage = burnDamagePerTick;
                hp -= damage;
                totalBurnDamage += damage;
                burnDmg -= damage;
                if (burnDmg <= 0) {
                    burnTicks = 0;
                }
            }
            if (currentTick + killTime == 100) {
              specBar += 30;
            }
        }
      } else if (weaponcase == 4) { // Soulreaper with Burning Claws
        hp = 700;
        int burnDmg = 0;
        int burnTicks = 0;
        int burnDamagePerTick = 0;
        while (specBar >= 30) {
            int newBurnDamage = clawSpec();
            burnDmg += newBurnDamage;
            if (newBurnDamage > 0 && burnTicks == 0) {
                burnTicks = 10; // 40 ticks / 4 = 10 "damage instances"
                burnDamagePerTick = burnDmg / burnTicks;
            }
            specBar -= 30;
            killTime += 4;
        }
        currentTick = 0;
        while (hp > 0) {
            defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
            currentTick ++;
            if (currentTick % 5 == 0) {
                if ((currentTick + killTime > 103) && (specBar == 10)) {
                  int newBurnDamage = clawSpec();
                  burnDmg += newBurnDamage;
                  if ((newBurnDamage > 0) && (burnTicks == 0)) {
                    burnTicks = 10;
                    burnDamagePerTick = burnDmg / burnTicks;
                  }
                  killTime --;
                  specBar = 0;
                } else {
                  findMaxAttackRoll(1);
                  findMaxDefRoll();
                  accuracy = calcAccuracy(1);
                  soulreaperAxeHit(accuracy);
                }
            }
            if (currentTick % 4 == 0 && burnDmg > 0) {
                int damage = burnDamagePerTick;
                hp -= damage;
                totalBurnDamage += damage;
                burnDmg -= damage;
                if (burnDmg <= 0) {
                    burnTicks = 0;
                }
            }
        }
    } else if (weaponcase == 5) { // Scythe with Voidwaker
        hp = 700;
        boolean voidwakerReady = false;
        while (hp > 0) {
            defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
            currentTick ++;
            if (currentTick % 5 == 0) {
                if (voidwakerReady && specBar >= 50) {
                    hp -= voidwakerSpec();
                    specBar -= 50;
                    killTime --;
                } else {
                    findMaxAttackRoll(0);
                    findMaxDefRoll();
                    accuracy  = calcAccuracy(1);
                    scytheSwing(accuracy);
                    if (!voidwakerReady) {
                        voidwakerReady = true;
                    }
                }
            }
        }
    } else if (weaponcase == 6) { // Soulreaper with Voidwaker
        hp = 700;
        boolean voidwakerReady = false;
        while (hp > 0) {
            defLvl = 215 - (70 - (int)Math.ceil(hp / 10));
            currentTick ++;
            if (currentTick % 5 == 0) {
                if (voidwakerReady && specBar >= 50) {
                    hp -= voidwakerSpec();
                    specBar -= 50;
                    killTime --;
                } else {
                    findMaxAttackRoll(1);
                    findMaxDefRoll();
                    accuracy = calcAccuracy(1);
                    soulreaperAxeHit(accuracy);
                    if (!voidwakerReady) {
                        voidwakerReady = true;
                    }
                }
            }
        }
    }
    killTime += currentTick;
}

public static void main(String[] args) {
    System.out.println("");
    VardorvisTest VT = new VardorvisTest(0);
    ArrayList<Double> times = new ArrayList<Double>();
    int totalBurnDamage = 0;
    int specTotalDamage = 0;
    int runs = 1000001;
    try {
        runs = Integer.parseInt(args[0]);
    } catch (Exception ignored) {
    }
    // Run simulations for different cases
    int[][] cases = new int[][] {{1, 3, 5}, {2, 4, 6}};
    String[] caseNames = new String[] {"Scythe", "Soulreaper"};

    for (int i = 0; i < cases.length; i++) {
        for (int j = 0; j < cases[i].length; j++) {
            int weaponcase = cases[i][j];
            times = new ArrayList<Double>();
            totalBurnDamage = 0;
            specTotalDamage = 0;
            System.out.println(caseNames[i] + (j == 1 ? " with Burning Claws" : "") + (j == 2 ? " with Voidwaker" : ""));
            for (int k = 0; k < runs; k++) {
                VT = new VardorvisTest(weaponcase);
                VT.simulateKill();
                times.add(VT.killTime);
                totalBurnDamage += VT.totalBurnDamage;
                specTotalDamage += VT.specTotalDamage;
            }
            // Print results
            double totalTime = 0;
            for (double d: times) {
                totalTime += d;
            }
            double average = totalTime / times.size();
            String minutes = Integer.toString((int)Math.floor((average * 0.6) / 60));
            String seconds = Integer.toString((int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6));
            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }
            System.out.println("Mean average: \t\t\t" + average * 0.6 + " seconds.");
            Collections.sort(times);
            double median = (times.get((int)Math.floor(times.size() / 2))
                            + times.get((int)Math.ceil(times.size() / 2))) / 2;
            minutes = Integer.toString((int)Math.floor((median * 0.6) / 60));
            seconds = Integer.toString((int)((((median * 0.6)/60) - Math.floor((median * 0.6) / 60))*100*0.6));
            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }
            System.out.println("Median average: \t\t" + minutes + ":" + seconds);
            minutes = Integer.toString((int)Math.floor((times.get(0) * 0.6) / 60));
            seconds = Integer.toString((int)((((times.get(0) * 0.6)/60) - Math.floor((times.get(0) * 0.6) / 60))*100*0.6));
            System.out.println("Fastest kill in " + runs + " kills: \t" + minutes + ":" + seconds);
            minutes = Integer.toString((int)Math.floor((times.get(runs - 1) * 0.6) / 60));
            seconds = Integer.toString((int)((((times.get(runs - 1) * 0.6)/60) - Math.floor((times.get(runs - 1) * 0.6) / 60))*100*0.6));
            if (seconds.length() == 1) {
                seconds = "0" + seconds;
            }
            System.out.println("Slowest kill in " + runs + " kills: \t" + minutes + ":" + seconds);
            System.out.println("Average burn damage per kill " + (double) totalBurnDamage / runs);
            System.out.println("Average spec damage per kill " + (double) specTotalDamage / runs);
            System.out.println("------");
        }
    }
  }
}


