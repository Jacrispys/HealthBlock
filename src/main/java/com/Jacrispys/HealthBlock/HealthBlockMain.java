package main.java.com.Jacrispys.HealthBlock;

import main.java.com.Jacrispys.HealthBlock.commands.HealthBlockStart;
import org.bukkit.plugin.java.JavaPlugin;

public class HealthBlockMain extends JavaPlugin {

    @Override
    public void onEnable() {

        new HealthBlockStart(this);
    }
}
