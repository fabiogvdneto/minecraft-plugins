package com.github.fabiogvdneto.kits.repository;

import com.github.fabiogvdneto.common.repository.flatfile.GsonKeyedRepository;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

public class GsonKitRepository extends GsonKeyedRepository<String, KitData> implements KitRepository {

    public GsonKitRepository(Plugin plugin) {
        super(plugin.getDataPath().resolve("kits"), new GsonBuilder().setPrettyPrinting().create(), KitData.class);
    }

}
