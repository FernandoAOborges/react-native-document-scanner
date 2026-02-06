import { Button, View } from 'react-native';
import { scanDocument } from '@fernandoaoborges/react-native-document-scanner';

export default function App() {
  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 24 }}>
      <Button
        title="Scan"
        onPress={async () => {
          const res = await scanDocument({
            allowGallery: true,
            pageLimit: 3,
            returnJpeg: true,
            returnPdf: true,
          });
          console.log(res);
        }}
      />
    </View>
  );
}
