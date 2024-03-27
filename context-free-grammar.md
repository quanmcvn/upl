$$ Program \rightarrow \textbf{begin} \ Statements \ \textbf{end}  $$

$$ Statements \rightarrow \begin{equation*} \begin{aligned}
& \epsilon \\
& Statement \  Statements
\end{aligned} \end{equation*}
$$

$$ Statement \rightarrow \begin{equation*} \begin{aligned}

& IfThenElseStatement \\
& DoWhileStatement \\
& PrintStatement \\
& DeclarationStatement \\
& AssignmentStatement

\end{aligned} \end{equation*}
$$

$$ IfThenElseStatement \rightarrow IfThen \ MaybeElse $$

$$ IfThen \rightarrow \textbf{if (} \ Expression \ \textbf{) then \{} \ Statements \ \textbf{\}} $$

$$ MaybeElse \rightarrow \begin{equation*} \begin{aligned}
& \epsilon \\
& \textbf{else \{} \ Statements \ \textbf{\}}

\end{aligned} \end{equation*}
$$

$$ DoWhileStatement \rightarrow \textbf{do \{} \ Statements \ \textbf{\} while (} \ Expression \ \textbf{) ;} $$

$$ PrintStatement \rightarrow \textbf{print (} \ Expression \ \textbf{) ;}  $$

$$ DeclarationStatement \rightarrow \boldsymbol{TypeSpecifier} \ InitDeclarator \ \textbf{;} $$

$$ AssignmentStatement \rightarrow \boldsymbol{Identifier} \ \boldsymbol{=} \ Expression \ \textbf{;} $$

$$ InitDeclarator \rightarrow \begin{equation*} \begin{aligned}

& \boldsymbol{Identifier} \\
& \boldsymbol{Identifier} \ \boldsymbol{=} \ Expression
\end{aligned} \end{equation*}
$$

$$ Expression \rightarrow EqualityExpression $$

$$ EqualityExpression \rightarrow \begin{equation*} \begin{aligned}

& RelationalExpression \\
& EqualityExpression \ \textbf{==} \ RelationalExpression

\end{aligned} \end{equation*}
$$

$$ RelationalExpression \rightarrow \begin{equation*} \begin{aligned}

& AdditiveExpression \\
& RelationalExpression \ \textbf{>} \ AdditiveExpression \\
& RelationalExpression \ \textbf{>=} \ AdditiveExpression

\end{aligned} \end{equation*}
$$

$$ AdditiveExpression \rightarrow \begin{equation*} \begin{aligned}

& MultiplicativeExpression \\
& AdditiveExpression \ \textbf{+} \ MultiplicativeExpression

\end{aligned} \end{equation*}
$$

$$ MultiplicativeExpression \rightarrow \begin{equation*} \begin{aligned}

& PrimaryExpression \\
& MultiplicativeExpression \ \textbf{*} \ PrimaryExpression

\end{aligned} \end{equation*}
$$

<!-- $$ EqualityExpression \rightarrow RelationalExpression \ EqualityExpression' $$

$$ EqualityExpression' \rightarrow \begin{equation*} \begin{aligned}
& \epsilon \\
& \textbf{==} \ RelationalExpression \ EqualityExpression'

\end{aligned} \end{equation*}
$$

$$ RelationalExpression \rightarrow AdditiveExpression \ RelationalExpression' $$

$$ RelationalExpression' \rightarrow \begin{equation*} \begin{aligned}

& \epsilon \\
& \textbf{>} \ AdditiveExpression \ RelationalExpression' \\
& \textbf{>=} \ AdditiveExpression \ RelationalExpression'

\end{aligned} \end{equation*}
$$

$$ AdditiveExpression \rightarrow \ MultiplicativeExpression \ AdditiveExpression' $$

$$ AdditiveExpression' \rightarrow \begin{equation*} \begin{aligned}

& \epsilon \\
& \textbf{+} \ MultiplicativeExpression \ AdditiveExpression'

\end{aligned} \end{equation*}
$$

$$ MultiplicativeExpression \rightarrow PrimaryExpression \ MultiplicativeExpression' $$

$$ MultiplicativeExpression' \rightarrow \begin{equation*} \begin{aligned}

& \epsilon \\
& \textbf{*} \ PrimaryExpression \ MultiplicativeExpression'

\end{aligned} \end{equation*}
$$ -->

$$ PrimaryExpression \rightarrow \begin{equation*} \begin{aligned}

& \boldsymbol{Identifier} \\
& \boldsymbol{Number} \\
& \textbf{(} \ Expression \ \textbf{)}

\end{aligned} \end{equation*}
$$
