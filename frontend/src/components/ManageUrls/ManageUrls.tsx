import styles from "./ManageUrls.module.css";

export type UrlEntry = {
    alias: string;
    fullUrl: string;
    shortUrl: string;
};

type Props = {
    urls: UrlEntry[];
    onDelete: (alias: string) => void;
};

const ManageUrls = ({ urls, onDelete }: Props) => {
    return (
        <table className={styles.table}>
            <thead>
            <tr>
                <th>Short URL</th>
                <th>Full URL</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            {urls.length === 0 ? (
                <tr>
                    <td colSpan={3} className={styles.noneFound}>
                        None found
                    </td>
                </tr>
            ) : (
                urls.map((u) => (
                    <tr key={u.alias}>
                        <td>
                            <a href={u.shortUrl} target="_blank" rel="noopener noreferrer">{u.shortUrl}</a>
                        </td>
                        <td className={styles.fullUrl}>{u.fullUrl}</td>
                        <td>
                            <button
                                className={styles.deleteButton}
                                onClick={() => onDelete(u.alias)}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))
            )}
            </tbody>
        </table>
    );
};

export default ManageUrls;
