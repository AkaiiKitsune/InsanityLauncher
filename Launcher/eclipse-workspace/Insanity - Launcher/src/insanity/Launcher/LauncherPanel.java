package insanity.Launcher;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class LauncherPanel extends JPanel implements SwingerEventListener {
    public static String path = System.getProperty("java.home");
    private Image background = Swinger.getResource("background.png");
    private Saver saver;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private STexturedButton playButton;
    private STexturedButton exitButton;
    private STexturedButton reduceButton;
    private STexturedButton ramButton;
    private SColoredBar progressBar;
    private JLabel infoLabel;
    RamSelector ramSelector;

    public LauncherPanel() {
        this.saver = new Saver(new File(Launcher.IF_DIR + "\\Launcher", "launcher.properties"));
        if (saver.get("javapath","").isEmpty())
            saver.set("javapath",getBetterJavaPath());
        else if (!new File(saver.get("javapath")).exists())
            saver.set("javapath",getBetterJavaPath());
        path = saver.get("javapath");
        this.usernameField = new JTextField(this.saver.get("username"));
        this.passwordField = new JPasswordField(this.saver.get("password"));
        if (this.passwordField.getText().length() != 0) {
            String password = this.saver.get("password");
            password = this.decrypt(password);
            this.passwordField = new JPasswordField(password);
        }

        this.playButton = new STexturedButton(Swinger.getResource("play.png"));
        this.exitButton = new STexturedButton(Swinger.getResource("exit.png"));
        this.reduceButton = new STexturedButton(Swinger.getResource("reduce.png"));
        this.ramButton = new STexturedButton(Swinger.getResource("ram.png"));
        this.progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        this.infoLabel = new JLabel("Clique sur jouer !", 0);
        this.ramSelector = new RamSelector(new File(Launcher.IF_DIR + "\\Launcher", "ram.txt"));
        this.setLayout((LayoutManager)null);
        this.usernameField.setForeground(Color.WHITE);
        this.usernameField.setFont(this.usernameField.getFont().deriveFont(20.0F));
        this.usernameField.setCaretColor(Color.WHITE);
        this.usernameField.setOpaque(false);
        this.usernameField.setBorder((Border)null);
        this.usernameField.setBounds(330, 192, 316, 37);
        this.add(this.usernameField);
        this.passwordField.setForeground(Color.WHITE);
        this.passwordField.setFont(this.passwordField.getFont().deriveFont(20.0F));
        this.passwordField.setCaretColor(Color.WHITE);
        this.passwordField.setOpaque(false);
        this.passwordField.setBorder((Border)null);
        this.passwordField.setBounds(330, 273, 316, 37);
        this.add(this.passwordField);
        this.ramButton.addEventListener(this);
        this.ramButton.setBounds(867, 0, 27, 18);
        this.add(this.ramButton);
        this.exitButton.addEventListener(this);
        this.exitButton.setBounds(923, 0, 48, 18);
        this.add(this.exitButton);
        this.reduceButton.addEventListener(this);
        this.reduceButton.setBounds(895, 0, 27, 18);
        this.add(this.reduceButton);
        this.playButton.addEventListener(this);
        this.playButton.setBounds(360, 343, 253, 64);
        this.add(this.playButton);
        this.infoLabel.setBounds(3, 572, 969, 22);
        this.infoLabel.setForeground(Color.WHITE);
        this.infoLabel.setFont(this.usernameField.getFont());
        this.add(this.infoLabel);
        this.progressBar.setBounds(3, 600, 969, 22);
        this.add(this.progressBar);
        this.ramButton.addEventListener(this);
        this.ramButton.setBounds(867, 0, 27, 18);
        this.add(this.ramButton);
    }

    public void onEvent(SwingerEvent e) {
        if (e.getSource() == this.exitButton) {
            Animator.fadeOutFrame(LauncherFrame.getInstace(), 2, new Runnable() {
                public void run() {
                    System.exit(0);
                }
            });
        }

        if (e.getSource() == this.reduceButton) {
            LauncherFrame.getInstace().setState(1);
        }

        if (e.getSource() == this.ramButton) {
            this.ramSelector.display();
        }

        if (e.getSource() == this.playButton) {
            this.ramSelector.save();
            this.setFieldEnabled(false);
            if (this.usernameField.getText().replaceAll(" ", "").length() == 0 || this.passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Erreur, veuillez entrer un pseudo et un mot de passe valide.", "Erreur identifiants", 0);
                this.setFieldEnabled(true);
                return;
            }

            Thread t = new Thread() {
                public void run() {
                    try {
                        Launcher.auth(LauncherPanel.this.usernameField.getText(), LauncherPanel.this.passwordField.getText());
                    } catch (AuthenticationException var4) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter : " + var4.getErrorModel().getErrorMessage(), "Erreur Connection", 0);
                        LauncherPanel.this.setFieldEnabled(true);
                        return;
                    }

                    LauncherPanel.this.saver.set("username", LauncherPanel.this.usernameField.getText());
                    LauncherPanel.this.saver.set("password", LauncherPanel.this.encrypt(LauncherPanel.this.passwordField.getText()));

                    try {
                        Launcher.update();
                    } catch (Exception var3) {
                        Launcher.interruptThread();
                        Launcher.getCrashReporter().catchError(var3, "Impossible de mettre a jour Insanity !");
                        LauncherPanel.this.setFieldEnabled(true);
                        return;
                    }

                    try {
                        Launcher.launch();
                    } catch (LaunchException var2) {
                        Launcher.getCrashReporter().catchError(var2, "Impossible de lancer Insanity !");
                        LauncherPanel.this.setFieldEnabled(true);
                    }
                }
            };
            t.start();
        }

    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Swinger.drawFullsizedImage(graphics, this, this.background);
    }

    private void setFieldEnabled(boolean enabled) {
        this.usernameField.setEnabled(enabled);
        this.passwordField.setEnabled(enabled);
        this.playButton.setEnabled(enabled);
        this.ramButton.setEnabled(enabled);
    }

    public SColoredBar getProgressBar() {
        return this.progressBar;
    }

    public void setinfoText(String text) {
        this.infoLabel.setText(text);
    }

    public RamSelector getRamSelector() {
        return this.ramSelector;
    }

    public String encrypt(String password) {
        String crypte = "";

        for(int i = 0; i < password.length(); ++i) {
            int c = password.charAt(i) ^ 48;
            crypte = crypte + (char)c;
        }

        return crypte;
    }

    public String decrypt(String password) {
        String aCrypter = "";
        if (this.passwordField.getText().length() != 0) {
            for(int i = 0; i < password.length(); ++i) {
                int c = password.charAt(i) ^ 48;
                aCrypter = aCrypter + (char)c;
            }
        }

        return aCrypter;
    }

    public static String getBetterJavaPath() {
        boolean win = false;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            win=true;
            File file = new File("F:\\Programs\\Minecraft\\runtime\\jre-x64");
            log("Request server files");
            for (File jdk:file.listFiles()) {
                return jdk.getPath();
            }
        }
        File better = new File(System.getProperty("java.home"));
        File parent = new File(System.getProperty("java.home")).getParentFile();
        for (File file:parent.listFiles()) {
            if (!new File(file,"bin").exists())
                continue;
            if (new File(file,"jmods").exists())
                continue;
            better = file;
        }
        
        log("Current path : "+better.getPath());
        return better.getPath();
    }

public static void log(String message) {
	System.out.println("[Launcher]: "+message);
	}
}