package wwz.pedro.vital.essencial;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PrefixType {

    DEFAULT("dMjgl", Rank.MEMBER, tag -> tag.getColor() + "§l" + tag.getName().toUpperCase() + " " + tag.getColor()),
    BRACES("LRBwT", Rank.STREAMER_PLUS, tag -> tag.getColor() + "{" + tag.getName() + "} "),
    BRACKETS("sJvjZ", Rank.ELITE, tag -> tag.getColor() + "[" + tag.getName() + "] "),
    BRACKETS_UPPER("fHYat", Rank.ELITE, tag -> tag.getColor() + "[" + tag.getName().toUpperCase() + "] "),
    COLOR("xOEsP", Rank.ELITE, Tag::getFormattedColor),
    PARENTHESIS("bvjLy", Rank.STREAMER_PLUS, tag -> tag.getColor() + "(" + tag.getName() + ") "),
    VANILLA("EDhtE", Rank.STREAMER_PLUS, tag -> tag.getColor() + "<" + tag.getName() + "> "),
    DEFAULT_BOLD("XspJC", Rank.ELITE, tag -> tag.getColor() + "§l" + tag.getName().toUpperCase() + " "),
    DEFAULT_GRAY("bWJnm", Rank.ELITE, tag -> tag.getColor() + "§l" + tag.getName().toUpperCase() + " §7"),
    DEFAULT_LOWER("wtFLH", Rank.STREAMER_PLUS, tag -> tag.getColor() + tag.getName() + " "),
    DEFAULT_WHITE("YnRcF", Rank.ELITE, tag -> tag.getColor() + "§l" + tag.getName().toUpperCase() + " §f");

    private final String uniqueCode;
    private final Rank rank;
    private final Formatter formatter;

    @Getter
    private static final PrefixType[] values;

    static {
        values = values();
    }

    public static PrefixType fromString(String string) {
        return Arrays.stream(getValues()).filter(prefixType -> prefixType.name().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public static PrefixType fromUniqueCode(String code) {
        return Arrays.stream(getValues()).filter(prefixType -> prefixType.getUniqueCode().equals(code)).findFirst().orElse(null);
    }

    public interface Formatter {
        String format(Tag tag);
    }
}
