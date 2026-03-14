import { useState } from 'react';
import { Button, Image, ScrollView, Text, View } from 'react-native';
import { scanDocument } from '@fernandoaoborges/react-native-document-scanner';

export default function App() {
  const [imageUri, setImageUri] = useState<string | null>(null);
  const [log, setLog] = useState('No scan yet');

  const handleScanDefault = async () => {
    try {
      setLog('Opening scanner (default)...');
      setImageUri(null);

      const res = await scanDocument();

      setLog(JSON.stringify(res, null, 2));

      const firstImage = res.images?.[0] ?? null;

      if (!res.canceled) {
        setImageUri(firstImage);
      }
    } catch (e) {
      setLog(`ERROR: ${JSON.stringify(e, null, 2)}`);
    }
  };

  const handleScanAdvanced = async () => {
    try {
      setLog('Opening scanner (advanced)...');
      setImageUri(null);

      const res = await scanDocument({
        pageLimit: 3,
        returnPdf: true,
        returnJpeg: true,
        allowGallery: true,
      });

      setLog(JSON.stringify(res, null, 2));

      const firstImage = res.images?.[0] ?? null;

      if (!res.canceled) {
        setImageUri(firstImage);
      }
    } catch (e) {
      setLog(`ERROR: ${JSON.stringify(e, null, 2)}`);
    }
  };

  return (
    <ScrollView
      contentContainerStyle={{
        padding: 24,
        flexGrow: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <Button title="Scan (default)" onPress={handleScanDefault} />
      <View style={{ height: 12 }} />
      <Button title="Scan (advanced)" onPress={handleScanAdvanced} />

      <View style={{ marginTop: 24, width: '100%' }}>
        <Text selectable>{log}</Text>

        {imageUri && (
          <Image
            source={{ uri: imageUri }}
            style={{
              width: 250,
              height: 250,
              marginTop: 20,
              borderRadius: 8,
            }}
            resizeMode="contain"
          />
        )}
      </View>
    </ScrollView>
  );
}
