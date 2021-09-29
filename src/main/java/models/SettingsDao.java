package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "settings")
public class SettingsDao {
    @DatabaseField(id = true)
    public String guildName;
    @DatabaseField
    public String prefix;
    @DatabaseField
    public int valueOfVolume;
    @DatabaseField
    public long showTrackProgress;
}