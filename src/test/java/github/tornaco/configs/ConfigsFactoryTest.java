package github.tornaco.configs;

import github.tornaco.configs.mapping.Def;
import github.tornaco.configs.mapping.Key;
import github.tornaco.configs.mapping.Value;
import lombok.Data;
import lombok.ToString;
import org.testng.annotations.Test;

public class ConfigsFactoryTest {

    @Test
    public void testCreate() throws Exception {
        SimpleConfigs configs = ConfigsFactory.create(SimpleConfigs.class, "int=1024");

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

        @Value("{ip:102.22.32.2, port:1080}")
        Server getServer();
    }

    @Data
    @ToString
    class Server {
        private String ip;
        private int port;
    }
}