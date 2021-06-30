package com.tmb.oneapp.productsexpservice.config;

import com.tmb.common.logger.TMBLogger;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.xml.transform.TransformerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


@Configuration
public class FopConfig implements ResourceLoaderAware {
    private static final TMBLogger<FopConfig> logger = new TMBLogger<>(FopConfig.class);
    private ResourceLoader resourceLoader;

    private void setupFopFiles() throws IOException {
        String[] files = new String[]{"tmblogo.png", "makerealchange.png", "tmbaddress.png", "e_app.xsl", "DBOzoneX.ttf", "DBOzoneX-Bold.ttf", "DBOzoneX-Italic.ttf", "fop-config.xml"};
        new File("./fop").mkdir();
        for (String file : files) {
            Resource configResource = this.resourceLoader.getResource("classpath:fop/" + file);
            InputStream inputStream = configResource.getInputStream();
            Files.copy(inputStream, new File("./fop/" + file).toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("done preparing fop file:"+file);
        }
        logger.info("finished preparing fop files");
    }
    @Bean
    public FopFactory getFopFactory() throws IOException, ConfigurationException {
        setupFopFiles();
        File fopConfigFile = new File("./fop/fop-config.xml");
        File baseFolder = new File(fopConfigFile.getParent());
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        org.apache.fop.configuration.Configuration cfg = cfgBuilder.buildFromFile(fopConfigFile);
        FopFactoryBuilder builder = new FopFactoryBuilder(baseFolder.toURI()).setConfiguration(cfg);
        return builder.build();
    }
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    @Bean
    public TransformerFactory getTransformerFactory() {
        return TransformerFactory.newInstance();
    }
}
