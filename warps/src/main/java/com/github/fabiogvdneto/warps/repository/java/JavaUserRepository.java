package com.github.fabiogvdneto.warps.repository.java;

import com.github.fabiogvdneto.common.repository.java.JavaKeyedRepository;
import com.github.fabiogvdneto.warps.repository.UserRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JavaUserRepository extends JavaKeyedRepository<UserData> implements UserRepository {

    public JavaUserRepository(Path dir) {
        super(dir);
    }

    @Override
    protected String getKey(UserData data) {
        return data.uid().toString();
    }

    @Override
    public int purge(int days) throws IOException {
        int purgeCount = 0;
        Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);

        for (String uid : fetchKeys()) {
            long lastSeen = Bukkit.getOfflinePlayer(uid).getLastSeen();

            if (lastSeen > 0 && Instant.ofEpochMilli(lastSeen).isBefore(limit)) {
                try {
                    deleteOne(uid);
                    purgeCount++;
                } catch (IOException e) {
                    // Nothing we can do to help here.
                }
            }
        }

        return purgeCount;
    }
}
