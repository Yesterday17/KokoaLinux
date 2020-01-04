## Additional Reading: How To Use CocoaInput Linux Alpha 0.0.0?

### Notice
There is a part for both vanilla launcher users and others: CocoaInput 3.1.5 + CocoaInput Linux will **absolutely** crash your game. That's because Axeryok made instance of CocoaInput private, which made CocoaInput Linux impossible to get it without using reflection.  
So if you'd like to use this version, you need to make `CocoaInput.instance` public, and compile the mod yourself.  
As CocoaInput is licensed under MMPL_J, I can provide a download link for it. 

Then if you're a vanilla launcher user, you can just follow the guide written in the blog. But if you use other launchers, you may need to configure it by yourself. The following example uses MultiMC.

### MultiMC
What should be mentioned is that CocoaInput Linux needs a lwjgl tweak. So all the thing is about tweaking lwjgl.  
Just create a new instance - Edit instance - Version - LWJGL2, and you can see a list of button at right, including 'Custom' and 'Edit'.  
Click 'Custom' first, and you'll find 'Edit' clickable. Then 'Edit' the document. You can copy the following content to your text editor:
```json
{
    "formatVersion": 1,
    "libraries": [
        {
            "downloads": {
                "classifiers": {
                    "natives-linux": {
                        "sha1": "7ff832a6eb9ab6a767f1ade2b548092d0fa64795",
                        "size": 10362,
                        "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar"
                    },
                    "natives-osx": {
                        "sha1": "53f9c919f34d2ca9de8c51fc4e1e8282029a9232",
                        "size": 12186,
                        "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar"
                    },
                    "natives-windows": {
                        "sha1": "385ee093e01f587f30ee1c8a2ee7d408fd732e16",
                        "size": 155179,
                        "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar"
                    }
                }
            },
            "extract": {
                "exclude": [
                    "META-INF/"
                ]
            },
            "name": "net.java.jinput:jinput-platform:2.0.5",
            "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows"
            }
        },
        {
            "downloads": {
                "artifact": {
                    "sha1": "39c7796b469a600f72380316f6b1f11db6c2c7c4",
                    "size": 208338,
                    "url": "https://libraries.minecraft.net/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar"
                }
            },
            "name": "net.java.jinput:jinput:2.0.5"
        },
        {
            "downloads": {
                "artifact": {
                    "sha1": "e12fe1fda814bd348c1579329c86943d2cd3c6a6",
                    "size": 7508,
                    "url": "https://libraries.minecraft.net/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar"
                }
            },
            "name": "net.java.jutils:jutils:1.0.0"
        },
        {
            "downloads": {
                "artifact": {
                    "sha1": "b04f3ee8f5e43fa3b162981b50bb72fe1acabb33",
                    "size": 22,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209.jar"
                },
                "classifiers": {
                    "natives-linux": {
                        "sha1": "931074f46c795d2f7b30ed6395df5715cfd7675b",
                        "size": 578680,
                        "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-linux.jar"
                    },
                    "natives-osx": {
                        "sha1": "bcab850f8f487c3f4c4dbabde778bb82bd1a40ed",
                        "size": 426822,
                        "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-osx.jar"
                    },
                    "natives-windows": {
                        "sha1": "b84d5102b9dbfabfeb5e43c7e2828d98a7fc80e0",
                        "size": 613748,
                        "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar"
                    }
                }
            },
            "extract": {
                "exclude": [
                    "META-INF/"
                ]
            },
            "name": "org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209",
            "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows"
            }
        },
        {
            "downloads": {
                "artifact": {
                    "sha1": "003839c5b8b0ee5ba10d8042604325a92e9e6e97",
                    "size": 1078507,
                    "url": "https://axer.jp/library/lwjgl-2.9.4-nightly-20150209-cocoainput.jar"
                }
            },
            "name": "org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209-cocoainput"
        },
        {
            "downloads": {
                "artifact": {
                    "sha1": "d51a7c040a721d13efdfbd34f8b257b2df882ad0",
                    "size": 173887,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar"
                }
            },
            "name": "org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209"
        }
    ],
    "name": "LWJGL 2",
    "releaseTime": "2015-02-16T13:01:35+00:00",
    "type": "release",
    "uid": "org.lwjgl",
    "version": "2.9.4-nightly-20150209-cocoainput",
    "volatile": false
}
```
Then you can enjoy typing with IME in game. Wow