package com.mycompany.app;

import io.agora.rtc.SDK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.agora.rtc.AgoraAudioPcmDataSender;
import io.agora.rtc.AgoraLocalAudioTrack;
import io.agora.rtc.AgoraMediaNodeFactory;
import io.agora.rtc.AgoraRtcConn;
import io.agora.rtc.AgoraService;
import io.agora.rtc.AgoraServiceConfig;
import io.agora.rtc.DefaultRtcConnObserver;
import io.agora.rtc.RtcConnInfo;

public class App {

    public static AgoraService service = new AgoraService();

    public static class ConnObserver extends DefaultRtcConnObserver {
        @Override
        public void onConnected(AgoraRtcConn conn, RtcConnInfo rtcConnInfo, int reason) {
            System.out.println("join success");

            AgoraMediaNodeFactory factory = service.createMediaNodeFactory();
            AgoraAudioPcmDataSender audioFrameSender = factory.createAudioPcmDataSender();
            AgoraLocalAudioTrack customAudioTrack = service.createCustomAudioTrackPcm(audioFrameSender);
            customAudioTrack.setEnabled(1);
            conn.getLocalUser().publishAudio(customAudioTrack);

            Path relativePath = Paths.get("/usr/local/voice-ai-server/my-app/src/main/java/com/mycompany/app/horse.pcm");

            byte[] data;
            try {
                data = Files.readAllBytes(relativePath);
                audioFrameSender.send(data, (int)System.currentTimeMillis(), 16/(1000/10), 2, 1, 16);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        String token = "007eJxTYJiWvmGOxRaPC7z6dTyOhTr8J6xmFdoffWCvaFc1892X5AIFhsQ0M4NkMzNL46RUMxNzU5PEpNSUpDQTw2SzVMM0S2PTFwElKQ2BjAwxxXysjAwQCOKzMJSkFpcwMAAA4xseHg==";
        SDK.load(); // ensure JNI library load
        AgoraServiceConfig config = new AgoraServiceConfig();
        config.setEnableAudioProcessor(0);
        config.setEnableAudioDevice(0);
        config.setEnableVideo(0);
        config.setContext(0);
        config.setAppId("af60c6693be64754abedbf41c6e1f935");
        service.initialize(config);

        AgoraRtcConn conn = service.agoraRtcConnCreate(null);

        conn.registerObserver(new ConnObserver());

        conn.connect(token, "test", "12138");

        

        Thread.sleep(20000);
        conn.disconnect();
        conn.destroy();
        service.destroy();
    }
}