package com.github.fabiogvdneto.warps.repository.gson;

import com.github.fabiogvdneto.common.repository.gson.GsonKeyedRepository;
import com.github.fabiogvdneto.warps.repository.UserRepository;
import com.github.fabiogvdneto.warps.repository.data.UserData;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GsonUserRepository extends GsonKeyedRepository<UserData> implements UserRepository {

    public GsonUserRepository(Path dir) {
        super(dir, new GsonBuilder().setPrettyPrinting().create(), UserData.class);
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
