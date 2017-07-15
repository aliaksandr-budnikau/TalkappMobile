package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.activity.ProgressCallback;
import talkapp.org.talkappmobile.service.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.service.impl.VoiceRecordingProcess;

/**
 * @author Budnikau Aliaksandr
 */
public interface AudioProcessesFactory {
    VoiceRecordingProcess createVoiceRecordingProcess(RecordedTrack recordedTrackBuffer, ProgressCallback progress);

    VoicePlayingProcess createVoicePlayingProcess(RecordedTrack recordedTrackBuffer);
}