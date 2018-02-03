package insanity.Launcher;

import com.sun.awt.AWTUtilities;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;
import javax.swing.JFrame;



public class LauncherFrame
  extends JFrame
{
  private static LauncherFrame instance;
  private LauncherPanel launcherPanel;
  private static CrashReporter crashReporter;
  
  public LauncherFrame()
  {
    this.setTitle("Insanity Launcher");
    this.setSize(975, 625);
    this.setDefaultCloseOperation(3);
    this.setLocationRelativeTo(null);
    this.setUndecorated(true);
    this.setIconImage(Swinger.getResource("icon.png"));
    this.setContentPane(this.launcherPanel = new LauncherPanel());
    AWTUtilities.setWindowOpacity(this, 0.0F);
    
    WindowMover mover = new WindowMover(this);
    this.addMouseListener(mover);
    this.addMouseMotionListener(mover);
    
    this.setVisible(true);
    
    Animator.fadeInFrame(this, 2);
  }
  
  public static void main(String[] args)
  {
    Swinger.setSystemLookNFeel();
    Swinger.setResourcePath("/insanity/Launcher/Resource");
    Launcher.IF_CRASHES_DIR.mkdirs();
    
    crashReporter = new CrashReporter("Insanity launcher", Launcher.IF_CRASHES_DIR);
    
    instance = new LauncherFrame();
  }
  
  public static LauncherFrame getInstace() {
    return instance;
  }
  
  public LauncherPanel getLauncherPanel() {
    return launcherPanel;
  }
  
  public static CrashReporter getCrashReporter() {
    return crashReporter;
  }
}
