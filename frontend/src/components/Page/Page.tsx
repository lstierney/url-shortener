import styles from "./Page.module.css";
import * as React from "react";

type PageProps = {
    title?: string;
    children: React.ReactNode;
};

const Page = ({ title, children }: PageProps) => {
    return (
        <div className={styles.page}>
            {title && <h1 className={styles.title}>{title}</h1>}
            <div className={styles.content}>{children}</div>
        </div>
    );
};

export default Page;

