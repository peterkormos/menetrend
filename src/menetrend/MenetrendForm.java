
package menetrend;

import javax.microedition.lcdui.*;
import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MenetrendForm extends Form implements CommandListener, ItemStateListener
{
  ChoiceGroup menetirany;
  DateField date;

  private final static int HOLIDAY_HONAP = 0;
  private final static int HOLIDAY_NAP = 1;

  /** @todo +
   * husvet A húsvét vasárnap a tavaszi napéjegyenl\u0151séget követ\u0151 els\u0151 holdtölte utáni els\u0151 vasárnap.
   * + punkosd  A pünkösd arr a utal hogy húsvét szombatjának ünnepe után vagyunk 50 nappal.*/
  int[][] holidays = new int[][]
                     {
                     {1, 1}, //jan. 1.
                     {3, 15}, //mar. 15.
                     {5, 1}, //maj. 1.
                     {8, 20}, //aug. 20.
                     {10, 23}, //okt. 23.
                     {11, 1}, //nov. 1.
                     {12, 25}, //dec. 25.
                     {12, 26}, //dec. 26.
                     {12, 31} //dec. 31.
  };
  private String[] menetiranyok;
  private Hashtable menetrend;

  class Buszjarat
  {
    private String indul;
    private String erkezik;

    private boolean megyMunkanap;
    private boolean megySzombat;
    private boolean megyMunkaszuneten; //vasarnap + unnep

    private boolean csakTanitaskor;

    public Buszjarat(String indul,
                     String erkezik,
                     boolean megyMunkanap,
                     boolean megySzombat,
                     boolean megyMunkaszuneten,
                     boolean csakTanitaskor
        )
    {
      this.indul = indul;
      this.erkezik = erkezik;

      this.megyMunkanap = megyMunkanap;
      this.megySzombat = megySzombat;
      this.megyMunkaszuneten = megyMunkaszuneten;
      this.csakTanitaskor = csakTanitaskor;
    }

    public boolean megyBusz(Date date)
    {
      Calendar currentDate = Calendar.getInstance();
      currentDate.setTime(date);

//      System.err.println(
//      currentDate.get(Calendar.MONTH) + " " + //0-tol
//      currentDate.get(Calendar.DAY_OF_MONTH) //1-tol
//      );

      //megyMunkanap;
      if(!megyMunkanap &&
         currentDate.get(currentDate.DAY_OF_WEEK) >= currentDate.MONDAY &&
         currentDate.get(currentDate.DAY_OF_WEEK) <= currentDate.FRIDAY)
        return false;

      //megySzombat;
      if(!megySzombat &&
         currentDate.get(currentDate.DAY_OF_WEEK) == currentDate.SATURDAY)
        return false;

      //megyMunkaszuneten;
      if(!megyMunkaszuneten &&
         currentDate.get(currentDate.DAY_OF_WEEK) == currentDate.SUNDAY)
        return false;

      for (int i = 0; i < holidays.length; i++)
      {
        if(!megyMunkaszuneten &&
        currentDate.get(currentDate.MONTH) == (holidays[i][HOLIDAY_HONAP]-1) &&
        currentDate.get(currentDate.DAY_OF_WEEK) == holidays[i][HOLIDAY_NAP])
          return false;
      }

//      this.csakTanitaskor = csakTanitaskor;
      /** @todo  csakTanitaskor*/

      return true;
    }

    public String toString()
    {
      return indul + "   |   " + erkezik;
    }
  }

  public void itemStateChanged(Item item)
  {
    printDisplay(true);
  }

  public MenetrendForm()
  {
    super("Menetrend");

    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    loadMenetrend();

    menetirany = new ChoiceGroup("Menetirany", Choice.EXCLUSIVE, menetiranyok,
                                             new Image[2]);

    date = new DateField("Datum", DateField.DATE);
    date.setDate(new Date());

    setItemStateListener(this);
    setCommandListener(this);

    addCommand(new Command("Exit", Command.EXIT, 1));
    addCommand(new Command("OK", Command.OK, 1));

    printDisplay(false);
  }

  private void loadMenetrend()
  {
    menetrend = new Hashtable();

    menetrend.put("Eger-Noszvaj", new Buszjarat[] {
                  new Buszjarat("11:11", "22:22", true, true, true, true),
                  new Buszjarat("03:03", "04:04", true, true, true, true),
                  new Buszjarat("05:05", "06:06", true, true, true, true)
    });

    menetrend.put("Noszvaj-Eger", new Buszjarat[] {
                  new Buszjarat("77:77", "88:88", true, true, true, true),
                  new Buszjarat("09:09", "10:10", true, true, true, true),
                  new Buszjarat("11:11", "12:12", true, true, true, true)
    });

    menetiranyok = new String[menetrend.size()];

    int i = 0;
    for (Enumeration e = menetrend.keys(); e.hasMoreElements(); )
      menetiranyok[i++] = (String)e.nextElement();
  }

  private void printDisplay(boolean printMenetrend)
  {
    deleteAll();

    append(menetirany);
    append(date);

    if(printMenetrend)
    {
      append(getStringItem("-------------------\n"));
      append(getStringItem("Indul   |   Érkezik\n"));
//      append(getStringItem("---------------\n"));

//      \u2550
//          \u256C
//          \u2567
//          \u2568
//          \u2560
//          \u2566
//          \u2569
//          \u2554\u2557
//          \u255A\u255D
//      \u2551
//          \u255F
//          \u255E
//          \u253C
//          \u2500
//
      Buszjarat[] buszok = (Buszjarat[])menetrend.get(menetirany.getString(menetirany.getSelectedIndex()));

      Date currentDate = date.getDate();
      for (int i = 0; i < buszok.length; i++)
        if(buszok[i].megyBusz(currentDate))
          append(getStringItem(buszok[i] + "\n"));
    }
  }

  private StringItem getStringItem(String text)
  {
    StringItem si = new StringItem(null, text);
//    si.setLayout(si.LAYOUT_NEWLINE_AFTER);
//    si.setLayout(si.LAYOUT_CENTER);
//    si.setLayout(si.LAYOUT_NEWLINE_BEFORE);

    return si;
  }

  public void commandAction(Command command, Displayable displayable)
  {
    if (command.getCommandType() == Command.EXIT)
    {
      // stop the MIDlet
      MenetrendMain.quitApp();
    }
    else if (command.getCommandType() == Command.OK)
    {
      printDisplay(true);
    }
  }
}
