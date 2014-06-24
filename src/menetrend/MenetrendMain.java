package menetrend;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

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
public class MenetrendMain extends MIDlet
{
  static MenetrendMain instance;
  MenetrendForm displayable = new MenetrendForm();
  public MenetrendMain()
  {
    instance = this;
  }

  public void startApp()
  {
    Display.getDisplay(this).setCurrent(displayable);
  }

  public void pauseApp()
  {
  }

  public void destroyApp(boolean unconditional)
  {
  }

  public static void quitApp()
  {
    instance.destroyApp(true);
    instance.notifyDestroyed();
    instance = null;
  }

}
