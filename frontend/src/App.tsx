import Page from "./components/Page/Page";
import UrlManager from "./components/UrlManager/UrlManager";

const App = () => {
    return (
        <Page title="URL Shortener">
            <UrlManager />
        </Page>
    );
};

export default App;
