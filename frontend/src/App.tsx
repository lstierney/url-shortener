import Page from "./components/Page/Page";
import Card from "./components/Card/Card";
import ShortenUrl from "./components/ShortenUrl/ShortenUrl";

const App = () => {
    return (
        <Page>
            <Card title="Shorten URL">
                <ShortenUrl />
            </Card>
        </Page>
    );
};

export default App;
