# Configs

## Code sample
```java
  @Test
    public void testCreate() throws Exception {
        SimpleConfigs configs = ConfigsFactory.create(SimpleConfigs.class, "prop_file_path.prop");

        System.out.println(configs.getString());
        System.out.println(configs.getInt());
        System.out.println(configs.getServer());
    }

    interface SimpleConfigs extends Configs {
        @Key("int")
        @Def("23")
        int getInt();

        @Value("Hello, World!")
        String getString();

        @Def("{ip:102.22.32.2, port:1080}")
        Server getServer();
    }

    @Data
    @ToString
    class Server {
        private String ip;
        private int port;
    }
```

```
DDDDD
XXX
```
