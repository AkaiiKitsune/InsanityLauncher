package insanity.Launcher;

import fr.theshark34.openauth.AuthPoints;
import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openauth.Authenticator;
import fr.theshark34.openauth.model.AuthAgent;
import fr.theshark34.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import java.io.File;
import java.util.Arrays;

public class Launcher {
    public static final GameVersion IF_VERSION;
    public static final GameInfos IF_INFOS;
    public static final File IF_DIR;
    public static final File IF_CRASHES_DIR;
    private static CrashReporter crashReporter;
    private static AuthInfos authInfos;
    private static Thread updateThread;

    static {
        IF_VERSION = new GameVersion("1.12", GameType.V1_8_HIGHER);
        IF_INFOS = new GameInfos("Insanity", IF_VERSION, new GameTweak[]{GameTweak.FORGE});
        IF_DIR = IF_INFOS.getGameDir();
        IF_CRASHES_DIR = new File(IF_DIR + "\\Launcher", "crashes");
        crashReporter = new CrashReporter("Insanity Launcher", IF_CRASHES_DIR);
    }

    public Launcher() {
    }

    public static void auth(String username, String password) throws AuthenticationException {
        Authenticator authentificator = new Authenticator("https://authserver.mojang.com/", AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse response = authentificator.authenticate(AuthAgent.MINECRAFT, username, password, "");
        authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
    }

    public static void update() throws Exception {
        SUpdate su = new SUpdate("http://www.insanitymc.dyjix.fr/s-update/insanity/", IF_DIR);
        updateThread = new Thread() {
            private int val;
            private int max;

            public void run() {
                while(!this.isInterrupted()) {
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstace().getLauncherPanel().setinfoText("Verification des fichiers");
                    } else {
                        this.max = (int)(BarAPI.getNumberOfTotalBytesToDownload() / 1000L);
                        this.val = (int)(BarAPI.getNumberOfTotalDownloadedBytes() / 1000L);
                        LauncherFrame.getInstace().getLauncherPanel().getProgressBar().setMaximum(this.max);
                        LauncherFrame.getInstace().getLauncherPanel().getProgressBar().setValue(this.val);
                        LauncherFrame.getInstace().getLauncherPanel().setinfoText("Downloading files : " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(this.val, this.max) + " %");
                    }
                }

            }
        };
        updateThread.start();
        su.start();
        updateThread.interrupt();
        FileDeleter.check();
    }

    public static void launch() throws LaunchException {
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(IF_INFOS, GameFolder.BASIC, authInfos);
        String[] ramArguments = LauncherFrame.getInstace().getLauncherPanel().getRamSelector().getRamArguments();
        profile.getVmArgs().addAll(Arrays.asList(ramArguments));
        profile.getVmArgs().add("-Xverify:none");
        ExternalLauncher launcher = new ExternalLauncher(profile);
        Process p = launcher.launch();

        try {
            Thread.sleep(5000L);
            LauncherFrame.getInstace().setVisible(false);
            p.waitFor();
        } catch (InterruptedException var5) {
            var5.printStackTrace();
        }

        System.exit(0);
    }

    public static void interruptThread() {
        updateThread.interrupt();
    }

    public static CrashReporter getCrashReporter() {
        return crashReporter;
    }
}
