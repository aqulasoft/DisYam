package com.aqulasoft.disyam.models.bot;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class SettingsOptional {
    Optional<String> prefix;
    Optional<Integer> volume;
    Optional<Boolean> showTrackPosition;

    public SettingsOptional() {

        prefix = Optional.empty();
        volume = Optional.empty();
        showTrackPosition = Optional.empty();
    }
}
