package net.tiffit.wynnforge.support.discord;

import me.paulhobbel.discordrp.api.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class WynncraftRichPresence extends DiscordRichPresence {

    private String originalDetails;

    public WynncraftRichPresence(Builder builder) {
        super(builder);
        originalDetails = builder.originalDetails;
        largeImageKey = "wynncraft";
        largeImageText = Minecraft.getMinecraft().getSession().getUsername();
        smallImageText = "";
        details = originalDetails;
        startTimestamp = System.currentTimeMillis() / 1000;
    }

    @Override
    public Builder buildUpon() {
        return new Builder(this);
    }

    public static class Builder extends DiscordRichPresence.BaseBuilder<Builder, DiscordRichPresence> {
        private String originalDetails;

        public Builder() {

        }

        Builder(WynncraftRichPresence parent) {
            super(parent);
            originalDetails = parent.originalDetails;
        }

        @Override
        public Builder details(String details) {
            originalDetails = details;
            return getThis();
        }

        @Override
        public Builder startTimestamp(long startTimestamp) {
        	return super.startTimestamp(startTimestamp);
        }

        @Override
        public WynncraftRichPresence build() {
            return new WynncraftRichPresence(this);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}