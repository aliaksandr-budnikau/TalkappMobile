package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.service.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.service.impl.VoiceRecordingProcess;

/**
 * @author Budnikau Aliaksandr
 */
public interface AudioProcessesFactory {
    VoiceRecordingProcess createVoiceRecordingProcess(RecordedTrack recordedTrackBuffer);

    VoicePlayingProcess createVoicePlayingProcess(RecordedTrack recordedTrackBuffer);
}