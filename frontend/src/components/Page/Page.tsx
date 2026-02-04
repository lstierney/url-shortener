import styles from "./Page.module.css";
import * as React from "react";

type PageProps = {
    children: React.ReactNode;
};

const Page = ({ children }: PageProps) => {
    return <div className={styles.page}>{children}</div>;
};

export default Page;
