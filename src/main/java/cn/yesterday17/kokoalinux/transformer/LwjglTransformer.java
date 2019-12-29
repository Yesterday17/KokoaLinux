package cn.yesterday17.kokoalinux.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Set;

public class LwjglTransformer implements IClassTransformer {
    public static void prepare(LaunchClassLoader classLoader) {
        try {
            // At this time, libraries like lwjgl has been loaded
            // That's Why LaunchWrapper added them to ClassLoaderExclusion
            // Because they'll cause an exception after reloading.

            // Protect org.lwjgl.(?!opengl)
            String[] lwjgl = new String[]{
                    "B", "D", "J", "L", "M", "P", "S", "W", // Classes
                    "input", "openal", "opencl", "opengles",// Packages
            };
            for (String append : lwjgl) {
                classLoader.addTransformerExclusion("org.lwjgl." + append);
            }

            // Protect org.lwjgl.opengl.(?!LinuxEvent)
            String[] opengl = new String[]{
                    "A", "B", "C", "D", "E", "F", "G", "I", "K",
                    "LinuxA", "LinuxB", "LinuxC", "LinuxD", "LinuxK", "LinuxM", "LinuxP", "LinuxA",
                    "M", "N", "O", "P", "R", "S", "U", "W", "X",
            };
            for (String append : opengl) {
                classLoader.addTransformerExclusion("org.lwjgl.opengl." + append);
            }

            // Make org.lwjgl Tweakable
            Field f = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            f.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>) f.get(classLoader);
            classLoaderExceptions.remove("org.lwjgl.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("org.lwjgl.opengl.LinuxEvent")) {
            return LinuxEventTweak.getBytes();
        }
        return basicClass;
    }

    static class LinuxEventTweak {
        static byte[] getBytes() {
            return Base64.getDecoder().decode(
                    "yv66vgAAADQAwAoAJACWCQAjAJcKACMAmAkAIwCZCgCaAJsKAJoAnAoAmgCdCgAjAJ4JACMAnwoA" +
                            "IwCgCgAjAKEKACMAogoAIwCjCgAjAKQKACMApQoAIwCmCgAjAKcKACMAqAoAIwCpCgAjAKoKACMA" +
                            "qwoAIwCsCgAjAK0KACMArgoAIwCvCgAjALAKACMAsQoAIwCyCgAjALMKACMAtAoAIwC1CgAjALYK" +
                            "ACMAtwoAIwC4BwC5BwC6AQAHRm9jdXNJbgEAAUkBAA1Db25zdGFudFZhbHVlAwAAAAkBAAhGb2N1" +
                            "c091dAMAAAAKAQAIS2V5UHJlc3MDAAAAAgEACktleVJlbGVhc2UDAAAAAwEAC0J1dHRvblByZXNz" +
                            "AwAAAAQBAA1CdXR0b25SZWxlYXNlAwAAAAUBAAxNb3Rpb25Ob3RpZnkDAAAABgEAC0VudGVyTm90" +
                            "aWZ5AwAAAAcBAAtMZWF2ZU5vdGlmeQMAAAAIAQALVW5tYXBOb3RpZnkDAAAAEgEACU1hcE5vdGlm" +
                            "eQMAAAATAQAGRXhwb3NlAwAAAAwBAA9Db25maWd1cmVOb3RpZnkDAAAAFgEADUNsaWVudE1lc3Nh" +
                            "Z2UDAAAAIQEADGV2ZW50X2J1ZmZlcgEAFUxqYXZhL25pby9CeXRlQnVmZmVyOwEAEmZpbmFsRXZl" +
                            "bnRGaWx0ZXJlZAEAAVoBAAllbmFibGVJTUUBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51" +
                            "bWJlclRhYmxlAQARY3JlYXRlRXZlbnRCdWZmZXIBABcoKUxqYXZhL25pby9CeXRlQnVmZmVyOwEA" +
                            "CGNvcHlGcm9tAQAgKExvcmcvbHdqZ2wvb3BlbmdsL0xpbnV4RXZlbnQ7KVYBAApnZXRQZW5kaW5n" +
                            "AQAEKEopSQEACXNlbmRFdmVudAEAByhKSlpKKVYBAApuU2VuZEV2ZW50AQAcKExqYXZhL25pby9C" +
                            "eXRlQnVmZmVyO0pKWkopVgEAC2ZpbHRlckV2ZW50AQAEKEopWgEADGZpbHRlckV2ZW50WAEADVN0" +
                            "YWNrTWFwVGFibGUBAAxuRmlsdGVyRXZlbnQBABkoTGphdmEvbmlvL0J5dGVCdWZmZXI7SilaAQAJ" +
                            "bmV4dEV2ZW50AQAEKEopVgEACm5OZXh0RXZlbnQBABkoSkxqYXZhL25pby9CeXRlQnVmZmVyOylW" +
                            "AQAHZ2V0VHlwZQEAAygpSQEACG5HZXRUeXBlAQAYKExqYXZhL25pby9CeXRlQnVmZmVyOylJAQAJ" +
                            "Z2V0V2luZG93AQADKClKAQAKbkdldFdpbmRvdwEAGChMamF2YS9uaW8vQnl0ZUJ1ZmZlcjspSgEA" +
                            "CXNldFdpbmRvdwEACm5TZXRXaW5kb3cBABkoTGphdmEvbmlvL0J5dGVCdWZmZXI7SilWAQAMZ2V0" +
                            "Rm9jdXNNb2RlAQANbkdldEZvY3VzTW9kZQEADmdldEZvY3VzRGV0YWlsAQAPbkdldEZvY3VzRGV0" +
                            "YWlsAQAUZ2V0Q2xpZW50TWVzc2FnZVR5cGUBABVuR2V0Q2xpZW50TWVzc2FnZVR5cGUBAA1nZXRD" +
                            "bGllbnREYXRhAQAEKEkpSQEADm5HZXRDbGllbnREYXRhAQAZKExqYXZhL25pby9CeXRlQnVmZmVy" +
                            "O0kpSQEAD2dldENsaWVudEZvcm1hdAEAEG5HZXRDbGllbnRGb3JtYXQBAA1nZXRCdXR0b25UaW1l" +
                            "AQAObkdldEJ1dHRvblRpbWUBAA5nZXRCdXR0b25TdGF0ZQEAD25HZXRCdXR0b25TdGF0ZQEADWdl" +
                            "dEJ1dHRvblR5cGUBAA5uR2V0QnV0dG9uVHlwZQEAD2dldEJ1dHRvbkJ1dHRvbgEAEG5HZXRCdXR0" +
                            "b25CdXR0b24BAA1nZXRCdXR0b25Sb290AQAObkdldEJ1dHRvblJvb3QBAA5nZXRCdXR0b25YUm9v" +
                            "dAEAD25HZXRCdXR0b25YUm9vdAEADmdldEJ1dHRvbllSb290AQAPbkdldEJ1dHRvbllSb290AQAK" +
                            "Z2V0QnV0dG9uWAEAC25HZXRCdXR0b25YAQAKZ2V0QnV0dG9uWQEAC25HZXRCdXR0b25ZAQANZ2V0" +
                            "S2V5QWRkcmVzcwEADm5HZXRLZXlBZGRyZXNzAQAKZ2V0S2V5VGltZQEAC25HZXRLZXlUaW1lAQAK" +
                            "Z2V0S2V5VHlwZQEAC25HZXRLZXlUeXBlAQANZ2V0S2V5S2V5Q29kZQEADm5HZXRLZXlLZXlDb2Rl" +
                            "AQALZ2V0S2V5U3RhdGUBAAxuR2V0S2V5U3RhdGUBAAg8Y2xpbml0PgEAClNvdXJjZUZpbGUBAA9M" +
                            "aW51eEV2ZW50LmphdmEMAEgASQwARQBGDABMAE0MAEMARAcAuwwAvABhDAC9AL4MALwAvwwAVABV" +
                            "DABHAEYMAFoAWwwAXgBfDABYAFcMAGIAYwwAZgBnDABpAGoMAGwAYwwAbgBjDABwAGcMAHMAdAwA" +
                            "dgBjDAB4AGcMAHoAYwwAfABjDAB+AGMMAIAAZwwAggBjDACEAGMMAIYAYwwAiABjDACKAGcMAIwA" +
                            "YwwAjgBjDACQAGMMAJIAYwEAG29yZy9sd2pnbC9vcGVuZ2wvTGludXhFdmVudAEAEGphdmEvbGFu" +
                            "Zy9PYmplY3QBABNqYXZhL25pby9CeXRlQnVmZmVyAQAIcG9zaXRpb24BAANwdXQBACwoTGphdmEv" +
                            "bmlvL0J5dGVCdWZmZXI7KUxqYXZhL25pby9CeXRlQnVmZmVyOwEAFChJKUxqYXZhL25pby9CdWZm" +
                            "ZXI7ACEAIwAkAAAAEQAZACUAJgABACcAAAACACgAGQApACYAAQAnAAAAAgAqABkAKwAmAAEAJwAA" +
                            "AAIALAAZAC0AJgABACcAAAACAC4AGQAvACYAAQAnAAAAAgAwABkAMQAmAAEAJwAAAAIAMgAZADMA" +
                            "JgABACcAAAACADQAGQA1ACYAAQAnAAAAAgA2ABkANwAmAAEAJwAAAAIAOAAZADkAJgABACcAAAAC" +
                            "ADoAGQA7ACYAAQAnAAAAAgA8ABkAPQAmAAEAJwAAAAIAPgAZAD8AJgABACcAAAACAEAAGQBBACYA" +
                            "AQAnAAAAAgBCABIAQwBEAAAAAgBFAEYAAAAJAEcARgAAADgAAABIAEkAAQBKAAAANQACAAEAAAAR" +
                            "KrcAASoDtQACKrgAA7UABLEAAAABAEsAAAASAAQAAAA/AAQAPAAJAEAAEABBAQoATABNAAAAAQBO" +
                            "AE8AAQBKAAAAWwACAAQAAAAvKrQABLYABT0rtAAEtgAFPiq0AAQrtAAEtgAGVyq0AAQctgAHVyu0" +
                            "AAQdtgAHV7EAAAABAEsAAAAaAAYAAABFAAgARgAQAEcAHABIACUASQAuAEoBCQBQAFEAAAABAFIA" +
                            "UwABAEoAAAAqAAgACAAAAA4qtAAEHyEVBRYGuAAIsQAAAAEASwAAAAoAAgAAAE8ADQBQAQoAVABV" +
                            "AAAAAQBWAFcAAQBKAAAAHQABAAMAAAAFKrQAAqwAAAABAEsAAAAGAAEAAABUAAEAWABXAAEASgAA" +
                            "ADYAAwADAAAAE7IACZkADiq0AAQfuAAKpwAEA6wAAAACAEsAAAAGAAEAAABYAFkAAAAFAAIRQAEB" +
                            "CgBaAFsAAAABAFwAXQABAEoAAAAyAAQAAwAAABIfKrQABLgACyoqCbYADLUAArEAAAABAEsAAAAO" +
                            "AAMAAABdAAgAXgARAF8BCgBeAF8AAAABAGAAYQABAEoAAAAgAAEAAQAAAAgqtAAEuAANrAAAAAEA" +
                            "SwAAAAYAAQAAAGMBCgBiAGMAAAABAGQAZQABAEoAAAAgAAIAAQAAAAgqtAAEuAAOrQAAAAEASwAA" +
                            "AAYAAQAAAGgBCgBmAGcAAAABAGgAXQABAEoAAAAlAAMAAwAAAAkqtAAEH7gAD7EAAAABAEsAAAAK" +
                            "AAIAAABtAAgAbgEKAGkAagAAAAEAawBhAAEASgAAACAAAQABAAAACCq0AAS4ABCsAAAAAQBLAAAA" +
                            "BgABAAAAdAEKAGwAYwAAAAEAbQBhAAEASgAAACAAAQABAAAACCq0AAS4ABGsAAAAAQBLAAAABgAB" +
                            "AAAAeQEKAG4AYwAAAAEAbwBlAAEASgAAACAAAgABAAAACCq0AAS4ABKtAAAAAQBLAAAABgABAAAA" +
                            "gAEKAHAAZwAAAAEAcQByAAEASgAAACEAAgACAAAACSq0AAQbuAATrAAAAAEASwAAAAYAAQAAAIUB" +
                            "CgBzAHQAAAABAHUAYQABAEoAAAAgAAEAAQAAAAgqtAAEuAAUrAAAAAEASwAAAAYAAQAAAIoBCgB2" +
                            "AGMAAAABAHcAZQABAEoAAAAgAAIAAQAAAAgqtAAEuAAVrQAAAAEASwAAAAYAAQAAAJEBCgB4AGcA" +
                            "AAABAHkAYQABAEoAAAAgAAEAAQAAAAgqtAAEuAAWrAAAAAEASwAAAAYAAQAAAJYBCgB6AGMAAAAB" +
                            "AHsAYQABAEoAAAAgAAEAAQAAAAgqtAAEuAAXrAAAAAEASwAAAAYAAQAAAJsBCgB8AGMAAAABAH0A" +
                            "YQABAEoAAAAgAAEAAQAAAAgqtAAEuAAYrAAAAAEASwAAAAYAAQAAAKABCgB+AGMAAAABAH8AZQAB" +
                            "AEoAAAAgAAIAAQAAAAgqtAAEuAAZrQAAAAEASwAAAAYAAQAAAKUBCgCAAGcAAAABAIEAYQABAEoA" +
                            "AAAgAAEAAQAAAAgqtAAEuAAarAAAAAEASwAAAAYAAQAAAKoBCgCCAGMAAAABAIMAYQABAEoAAAAg" +
                            "AAEAAQAAAAgqtAAEuAAbrAAAAAEASwAAAAYAAQAAAK8BCgCEAGMAAAABAIUAYQABAEoAAAAgAAEA" +
                            "AQAAAAgqtAAEuAAcrAAAAAEASwAAAAYAAQAAALQBCgCGAGMAAAABAIcAYQABAEoAAAAgAAEAAQAA" +
                            "AAgqtAAEuAAdrAAAAAEASwAAAAYAAQAAALkBCgCIAGMAAAABAIkAZQABAEoAAAAgAAIAAQAAAAgq" +
                            "tAAEuAAerQAAAAEASwAAAAYAAQAAAMABCgCKAGcAAAABAIsAZQABAEoAAAAhAAIAAQAAAAkqtAAE" +
                            "uAAfha0AAAABAEsAAAAGAAEAAADFAQoAjABjAAAAAQCNAGEAAQBKAAAAIAABAAEAAAAIKrQABLgA" +
                            "IKwAAAABAEsAAAAGAAEAAADKAQoAjgBjAAAAAQCPAGEAAQBKAAAAIAABAAEAAAAIKrQABLgAIawA" +
                            "AAABAEsAAAAGAAEAAADPAQoAkABjAAAAAQCRAGEAAQBKAAAAIAABAAEAAAAIKrQABLgAIqwAAAAB" +
                            "AEsAAAAGAAEAAADUAQoAkgBjAAAACACTAEkAAQBKAAAAHQABAAAAAAAFA7MACbEAAAABAEsAAAAG" +
                            "AAEAAAA9AAEAlAAAAAIAlQ==");
        }
    }
}
