package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.ProgressCallback;
import talkapp.org.talkappmobile.component.impl.VoicePlayingProcess;
import talkapp.org.talkappmobile.component.impl.VoiceRecordingProcess;

/**
 * @author Budnikau Aliaksandr
 */
public interface AudioProcessesFactory {
    VoiceRecordingProcess createVoiceRecordingProcess(RecordedTrack recordedTrackBuffer, ProgressCallback progress);

    VoicePlayingProcess createVoicePlayingProcess(RecordedTrack recordedTrackBuffer);
}