use std::fs;
use actix_web::{web, App, HttpServer, Responder};
use std::io::{Write};
use actix_files::Files;
use serde::{Serialize, Deserialize};
use actix_web::middleware::Logger;

#[derive(Serialize, Deserialize)]
struct Message {
    content: String
}

async fn get_logs() -> impl Responder {
    let content = fs::read_to_string("shared/logs/latest.log").unwrap_or("Fichier vide\n".into());

    format!("{}", content)
}

async fn add_logs(message: web::Query<Message>) -> impl Responder {
    let mut file = fs::OpenOptions::new()
        .write(true)
        .create(true)
        .append(true)
        .open("shared/logs/latest.log")
        .unwrap();

    write!(file, "{}\n", message.content).unwrap();
    return "Added log"
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    println!("Running server on port 3000\n");

    HttpServer::new(|| App::new()
        .wrap(Logger::default())
        .wrap(Logger::new("%a %{User-Agent}i"))
        .service(web::scope("/logs")
            .service(web::resource("")
                .route(web::get().to(get_logs))
                .route(web::post().to(add_logs))
            ))
            .service(Files::new("/apps", "shared/apps").show_files_listing())
    )
        .bind("0.0.0.0:3000")?
        .run()
        .await
}